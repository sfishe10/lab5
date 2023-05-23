public class InvalidInstruction extends Instruction {

    private final String id;

    public InvalidInstruction(String id) {
        this.id = id;
    }
    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.id;
    }

    public void printCode() {
        System.out.println("invalid instruction: " + id);
    }
}
