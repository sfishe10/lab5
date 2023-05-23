public class J extends Instruction {
    private final String id;
    private String opcode;
    private int address;

    public J(String id) {
        this.id = id;
        switch(id) {
            case "j" -> opcode = "000010";
            case "jal" -> opcode = "000011";
        }
    }

    public String getId() {
        return this.id;
    }

    public void setAddress(int address) {
        this.address = address;
    }
    public int getAddress() {
        return address;
    }

    public String getAddressCode() {
        return utility.signExtend(Integer.toBinaryString(address), 26);
    }

    @Override
    public String toString() {
        return this.id;
    }

    public void printCode() {
        System.out.println(opcode + " " + getAddressCode());
    }
}
