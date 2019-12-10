package ir.taxi1880.operatormanagement.model;

public class OperatorModel {
  private int operatorId;
  private String operatorName;
  private String operatorShift;

    public String getOperatorShift() {
        return operatorShift;
    }

    public void setOperatorShift(String operatorShift) {
        this.operatorShift = operatorShift;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(int operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
}
