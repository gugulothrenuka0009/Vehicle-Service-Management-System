package com.vehicleservice.dto;

import com.vehicleservice.entity.JobStatus;
import java.math.BigDecimal;
import java.util.List;

public class JobUpdateDto {
    private JobStatus status;
    private String diagnosisNotes;
    private String workSummary;
    private BigDecimal laborCost;
    private BigDecimal partsCost;
    private List<PartUsageItem> partsUsed;

    public static class PartUsageItem {
        private Long inventoryId;
        private Integer quantity;

        public PartUsageItem() {}
        public PartUsageItem(Long inventoryId, Integer quantity) {
            this.inventoryId = inventoryId;
            this.quantity = quantity;
        }

        public Long getInventoryId() { return inventoryId; }
        public void setInventoryId(Long inventoryId) { this.inventoryId = inventoryId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public JobUpdateDto() {}

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }

    public String getDiagnosisNotes() { return diagnosisNotes; }
    public void setDiagnosisNotes(String diagnosisNotes) { this.diagnosisNotes = diagnosisNotes; }

    public String getWorkSummary() { return workSummary; }
    public void setWorkSummary(String workSummary) { this.workSummary = workSummary; }

    public BigDecimal getLaborCost() { return laborCost; }
    public void setLaborCost(BigDecimal laborCost) { this.laborCost = laborCost; }

    public BigDecimal getPartsCost() { return partsCost; }
    public void setPartsCost(BigDecimal partsCost) { this.partsCost = partsCost; }

    public List<PartUsageItem> getPartsUsed() { return partsUsed; }
    public void setPartsUsed(List<PartUsageItem> partsUsed) { this.partsUsed = partsUsed; }
}
