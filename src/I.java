public class I extends Instruction {
    private String rt;
    private String rs;
    private int imm;
    private final String id;
    private String opcode;

    public I(String id){
        this.id = id;
        switch(id) {
            case "addi" -> opcode = "001000";
            case "beq" -> opcode = "000100";
            case "bne" -> opcode = "000101";
            case "lw" -> opcode = "100011";
            case "sw" -> opcode = "101011";
        }
    }

    public String getId() {
        return this.id;
    }



    public void setRt(String rt) {
        this.rt = rt;
    }
    public String getRt() { return rt; }


    public String getRtCode() {
        return utility.signExtend(Integer.toBinaryString(utility.getRegList().get(rt)), 5);
    }

    public void setRs(String rs) {
        this.rs = rs;
    }

    public String getRs() { return rs; }
    public String getRsCode() {
        return utility.signExtend(Integer.toBinaryString(utility.getRegList().get(rs)), 5);
    }

    public void setImm(int imm) {
        this.imm = imm;
    }
    public int getImm() { return imm; }

    public String getImmCode() {
        if (imm < 0) {
            return utility.signReduce(Integer.toBinaryString(imm), 16);
        }
        else {
            return utility.signExtend(Integer.toBinaryString(imm), 16);
        }
    }

    @Override
    public String toString() {
        return this.id + " " + imm;
    }

    public void printCode() {
        System.out.println(opcode + " " + getRsCode() + " " + getRtCode() + " " + getImmCode());
    }
}
