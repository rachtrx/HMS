package app.db;

import app.controller.CsvReaderService;
import app.utils.LoggerUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;



public final class Table {
    private final String tableName;
    private final Map<Table, Relationship> relationships;
    private final List<Row> rows;
    private final int pKeyCol;
    private final String filePath;
    private final List<String> headers;

    public Table(TableConfig tableConfig) throws IOException {
        this.tableName = tableConfig.getTableName();
        this.relationships = new HashMap<>();
        this.pKeyCol = tableConfig.getPKeyCol();
        this.filePath = tableConfig.getFilePath();
        this.headers = tableConfig.getHeaders();

        Set<String> uniqueKeys = new HashSet<>();
        this.rows = CsvReaderService.read(tableConfig.getFilePath())
            .stream()
            .filter(rowData -> {
                String pKey = rowData.get(this.pKeyCol); // Get primary key from row data
                // Try adding primary key to the set; if it's already present, filter it out
                return uniqueKeys.add(pKey);
            })
            .map(rowData -> new Row(this.pKeyCol, rowData))
            .collect(Collectors.toList());
    }

    public String getTableName() {
        return tableName;
    }
    
    public Map<Table, Relationship> getRelationships() {
        return relationships;
    }

    public Relationship getRelationship(Table targetTable) {
        return relationships.get(targetTable); // Simplified lookup
    }

    protected List<Row> getRows() { // Only accessible package level
        return rows;
    }

    public void addRow(List<String> row) {
        String pKey = row.get(pKeyCol);
        Row existingRow = findByVal(pKey, pKeyCol);
    
        if (existingRow == null) {
            rows.add(new Row(pKeyCol, row));
            LoggerUtils.info(this.getTableName() + ": New entry with primary key " + pKey + " added.");
        } else {
            existingRow.setData(row);
            LoggerUtils.info(this.getTableName() + ": Existing entry with primary key " + pKey + " already found, not added!");
        }
    }

    public void updateRow(List<String> row) {
        String pKey = row.get(pKeyCol);
        Row existingRow = findByVal(pKey, pKeyCol);
    
        if (existingRow == null) {
            LoggerUtils.info(this.getTableName() + ": Existing entry with primary key " + pKey + " not found, not updated!");
        } else {
            existingRow.setData(row);
            LoggerUtils.info(this.getTableName() + ": Existing entry with primary key " + pKey + " updated.");
        }
    }

    public void deleteRow(List<String> row) {
        String pKey = row.get(pKeyCol);
        Row existingRow = findByVal(pKey, pKeyCol);
    
        if (existingRow != null) {
            rows.remove(existingRow);
            LoggerUtils.info("Existing entry with primary key " + pKey + " removed.");
        } else {
            LoggerUtils.info("Existing entry with primary key " + pKey + " not found, not deleted (might have cascaded)!");
        }
    }

    public Row findByVal(String val, int colIdx) {
        for (Row row : rows) {
            if (!row.getData().isEmpty() && row.getData().size() > colIdx) {
                if (row.getData().get(colIdx).equals(val)) {
                    return row;
                }
            }
        }
        return null; // Return null if no match is found
    }

    public List<Row> findAllByVal(String val, int colIdx) {
        List<Row> matchingRows = new ArrayList<>();
    
        for (Row row : rows) { // Assuming `rows` is a list of Row objects in the table
            if (!row.getData().isEmpty() && row.getData().size() > colIdx) {
                // Check if the value in the specified column matches the val
                if (row.getData().get(colIdx).equals(val)) {
                    matchingRows.add(row); // Add the entire row data to the list if it matches
                }
            }
        }
        return matchingRows; // Return list of all matching rows
    }

    public void deleteRowsByIds(List<String> idsToDelete, int idColIdx) {
        List<Table> tablesToDeleteFrom = new ArrayList<>(relationships.keySet());

        if (idsToDelete.isEmpty()) return;

        // Log the IDs being deleted from the current table
        System.out.println("Deleting IDs from table " + this.getTableName() + ": " + idsToDelete);

        // Delete the rows from the current table
        rows.removeIf(row -> {
            if (row.getData().size() > idColIdx) {
                String rowId = row.getData().get(idColIdx);
                return idsToDelete.contains(rowId);
            }
            return false;
        });

        // Check relationships for cascading deletes
        for (Table relatedTable : tablesToDeleteFrom) {
            Relationship relationship = relationships.get(relatedTable);
            if (relationship != null) {
                switch (relationship.getDeleteBehavior()) {
                    case CASCADE:
                        // Collect the IDs to delete in the related table
                        List<String> relatedIdsToDelete = new ArrayList<>();
                        for (Row row : relatedTable.getRows()) {
                            if (row.getData().size() > relationship.getRemoteColIdx()) {
                                String relatedID = row.getData().get(relationship.getRemoteColIdx());
                                if (idsToDelete.contains(relatedID)) {
                                    relatedIdsToDelete.add(relatedID); // Prepare for deletion
                                }
                            }
                        }
                        System.out.println("Cascading delete from table " + relatedTable.getTableName() + ": " + relatedIdsToDelete);
                        relatedTable.deleteRowsByIds(relatedIdsToDelete, relationship.getRemoteColIdx());
                        break;
                    case SET_NULL:
                        int remoteColIdx = relationship.getRemoteColIdx();
                        for (Row row : relatedTable.getRows()) {
                            if (row.getData().size() > remoteColIdx) {
                                String foreignID = row.getData().get(remoteColIdx);
                                if (idsToDelete.contains(foreignID)) {
                                    System.out.println("Setting foreign key to null in table " + relatedTable.getTableName() + " for ID: " + foreignID);
                                    row.getData().set(remoteColIdx, null); // Set to null
                                }
                            }
                        }
                        break;
                    case NO_ACTION:
                        break;
                }
            }
        }
    }

    public Table addRelationship(Table remoteTable, int localColIdx, int remoteColIdx, DeleteBehavior deleteBehavior) {
        this.relationships.put(remoteTable, new Relationship(localColIdx, remoteColIdx, deleteBehavior));
        return this;
    }

    private List<String> collectValuesFromTable(int colIdx) {
        List<String> values = new ArrayList<>();
        for (Row row : this.getRows()) {
            if (row.getData().size() > colIdx) {
                values.add(row.getData().get(colIdx)); // Collect ID from the specified column index
            }
        }
        return values;
    }

    public void delRowsWithMissingParentIds(Table parentTable) {
        List<String> unmatchedIDs = this.getRowsWithMissingParentIds(parentTable);
        if (unmatchedIDs.isEmpty()) return;

        this.deleteRowsByIds(unmatchedIDs, getRelationship(parentTable).getLocalColIdx());
    }

    /**
     * Pass in the parent Table. This ensures that the parent table contains the child's FK
     * @param parentTable
     * @return
     */
    public List<String> getRowsWithMissingParentIds(Table parentTable) {
        List<String> unmatchedIDs = new ArrayList<>();
    
        Relationship relationship = getRelationship(parentTable);
    
        if (relationship == null) {
            return unmatchedIDs; // No relationship exists
        }
    
        int childColIdx = relationship.getLocalColIdx();
        int parentColIdx = relationship.getRemoteColIdx();
    
        // Collect all parent IDs from the parent table
        List<String> parentIds = parentTable.collectValuesFromTable(parentColIdx);
    
        // Check child rows against parent IDs
        for (Row childRow : this.getRows()) {
            if (childRow.getData().size() > childColIdx) {
                String childId = childRow.getData().get(childColIdx);
                if (!parentIds.contains(childId)) {
                    unmatchedIDs.add(childId);
                }
            }
        }
    
        return unmatchedIDs;
    }

    public void delRowsWithMissingChildIds(Table[] childTables) {
        Set<String> allParentIDs  = new HashSet<>();
        Set<String> allChildIDs = new HashSet<>();

        int parentColIdx = -1;
    
        for (Table childTable : childTables) {
            Relationship relationship = getRelationship(childTable);
            
            if (relationship == null) {
                continue;
            }

            if (parentColIdx == -1) {
                parentColIdx = relationship.getLocalColIdx();
            } else if (relationship.getLocalColIdx() != parentColIdx) System.out.println("Missing Child for parent found...");

            int childColIdx = relationship.getRemoteColIdx();
    
            List<String> parentIDs = this.collectValuesFromTable(parentColIdx);
            List<String> childIDs = childTable.collectValuesFromTable(childColIdx);
    
            allParentIDs.addAll(parentIDs);
            allChildIDs.addAll(childIDs);
        }

        allParentIDs.removeAll(allChildIDs);
        Set<String> unmatchedIDs = allParentIDs;

        if (!unmatchedIDs.isEmpty()) {
            this.deleteRowsByIds(new ArrayList<>(unmatchedIDs), parentColIdx);
        }
    }

    public void writeToCsv() throws IOException {
        List<List<String>> dataToWrite = this.rows.stream()
            .map(Row::getData)
            .collect(Collectors.toList());

        List<List<String>> outputData = new ArrayList<>();
        outputData.add(this.headers);
        outputData.addAll(dataToWrite);
    
        CsvReaderService.write(this.filePath, outputData);
    }
    
}
