/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author a80052136
 */
public class BIS {
    protected String idScope, acceptancePlan, category, stepName, subcontractor;
    protected String onADate, bisDate, stepStatus, stepInitiated, stepSubmitted;
    protected String rejected, resubmitted, stepAccepted;
    protected String site;
    
    public BIS (String idScope, String acceptancePlan, String category, 
            String stepName, String subcontractor, String onADate, 
            String bisDate, String stepStatus, String stepInitiated, 
            String stepSubmitted, String rejected, String resubmitted, 
            String stepAccepted, String site) {
        
        this.idScope = idScope;
        this.acceptancePlan = acceptancePlan;
        this.category = category;
        this.stepName = stepName;
        this.subcontractor = subcontractor;
        this.onADate = onADate;
        this.bisDate = bisDate;
        this.stepStatus = stepStatus;
        this.stepInitiated = stepInitiated;
        this.stepSubmitted = stepSubmitted;
        this.rejected = rejected;
        this.resubmitted = resubmitted;
        this.stepAccepted = stepAccepted;
        this.site = site;
    }

    public String getIdScope() {
        return idScope;
    }

    public String getAcceptancePlan() {
        return acceptancePlan;
    }

    public String getCategory() {
        return category;
    }

    public String getStepName() {
        return stepName;
    }

    public String getSubcontractor() {
        return subcontractor;
    }

    public String getOnADate() {
        return onADate;
    }

    public String getBisDate() {
        return bisDate;
    }

    public String getStepStatus() {
        return stepStatus;
    }

    public String getStepInitiated() {
        return stepInitiated;
    }

    public String getStepSubmitted() {
        return stepSubmitted;
    }

    public String getRejected() {
        return rejected;
    }

    public String getResubmitted() {
        return resubmitted;
    }

    public String getStepAccepted() {
        return stepAccepted;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public void setIdScope(String idScope) {
        this.idScope = idScope;
    }

    public void setAcceptancePlan(String acceptancePlan) {
        this.acceptancePlan = acceptancePlan;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public void setSubcontractor(String subcontractor) {
        this.subcontractor = subcontractor;
    }

    public void setOnADate(String onADate) {
        this.onADate = onADate;
    }

    public void setBisDate(String bisDate) {
        this.bisDate = bisDate;
    }

    public void setStepStatus(String stepStatus) {
        this.stepStatus = stepStatus;
    }

    public void setStepInitiated(String stepInitiated) {
        this.stepInitiated = stepInitiated;
    }

    public void setStepSubmitted(String stepSubmitted) {
        this.stepSubmitted = stepSubmitted;
    }

    public void setRejected(String rejected) {
        this.rejected = rejected;
    }

    public void setResubmitted(String resubmitted) {
        this.resubmitted = resubmitted;
    }

    public void setStepAccepted(String stepAccepted) {
        this.stepAccepted = stepAccepted;
    }

    @Override
    public String toString() {
        return "BIS{" + "idScope=" + idScope + ", acceptancePlan=" + acceptancePlan + ", category=" + category + ", stepName=" + stepName + ", subcontractor=" + subcontractor + ", onADate=" + onADate + ", bisDate=" + bisDate + ", stepStatus=" + stepStatus + ", stepInitiated=" + stepInitiated + ", stepSubmitted=" + stepSubmitted + ", rejected=" + rejected + ", resubmitted=" + resubmitted + ", stepAccepted=" + stepAccepted + '}';
    }

    

    
    
    
}
