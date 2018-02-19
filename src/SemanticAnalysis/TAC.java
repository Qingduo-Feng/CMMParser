package SemanticAnalysis;

public class TAC {
    private String op;
    private String arg1;
    private String arg2;
    private String address;

    public TAC(String op, String arg1, String arg2, String address){
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.address= address;
    }

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        stringBuilder.append(op);
        stringBuilder.append(",");
        stringBuilder.append(arg1);
        stringBuilder.append(",");
        stringBuilder.append(arg2);
        stringBuilder.append(",");
        stringBuilder.append(address);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public void backfill(String address){
        this.address = address;
    }

    public String getOp()
    {
        return op;
    }

    public String getArg1() {
        return arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public String getAddress() {
        return address;
    }

}
