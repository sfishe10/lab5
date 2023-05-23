public class R extends Instruction {
    private String rd;
    private String rs;
    private String rt;
    private final String id;
    private String funct;
    private int shamt = 0;

    public R(String id){
        this.id = id;
        switch (id) {
            case "add" -> funct = "100000";
            case "and" -> funct = "100100";
            case "or" -> funct = "100101";
            case "sll" -> funct = "000000";
            case "sub" -> funct = "100010";
            case "slt" -> funct = "101010";
            case "jr" -> funct = "001000";
        }
        if (id.equals("jr")) {
            rd = "$zero";
            rt = "$zero";
        }
        if (id.equals("sll")) {
            rs = "$zero";
        }
    }

    public String getId() {
        return id;
    }

    public String getRd() { return rd; }

    public String getRs() { return rs; }

    public String getRt() { return rt; }

    public void setRd(String rd) {
        this.rd = rd;
    }

    public String getRdCode() {
        return utility.signExtend(Integer.toBinaryString(utility.getRegList().get(rd)), 5);
    }
    public void setRs(String rs) {
        this.rs = rs;
    }

    public String getRsCode() {
        return utility.signExtend(Integer.toBinaryString(utility.getRegList().get(rs)), 5);
    }

    public void setRt(String rt) {
        this.rt = rt;
    }

    public String getRtCode() {
        return utility.signExtend(Integer.toBinaryString(utility.getRegList().get(rt)), 5);
    }

    public void setShamt(int shamt) {
        this.shamt = shamt;
    }

    public int getShamt() {
        return shamt;
    }

    public String getShamtCode() {
        return utility.signExtend(Integer.toBinaryString(shamt), 5);
    }

    @Override
    public String toString() {
        return this.id;
    }

    public void printCode() {
        System.out.println("000000 " + getRsCode() + " " + getRtCode() + " " + getRdCode() + " " + getShamtCode() + " " + funct);
    }


}
