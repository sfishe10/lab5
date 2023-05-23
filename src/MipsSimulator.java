import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MipsSimulator {
    private final int[] datamem = new int[8192];
    private int pc = 0;
    private int ghr_size = 2;
    private int[] ghr = new int[ghr_size];
    private int[] counters = new int[2];
    private final LinkedHashMap<String, Integer> regs = new LinkedHashMap<>();
    private final ArrayList<Instruction> insts;

    public MipsSimulator(ArrayList<Instruction> insts) {

        // set all registers to hold 0
        for (String reg : utility.getRegList().keySet()) {
            regs.put(reg, 0);
        }

        this.insts = insts;
        
        for (int i = 0; i < ghr_size; i++) {
            ghr[i] = 0;
        }
        for (int i=0; i < 2; i++) {
            counters[i] = 0;
        }
    }

    public void setGhr_size(int bits) {
        this.ghr_size = bits;
    }

    public void setReg(String reg, int num) {
        if (reg != "$0" && reg != "$zero") {
            regs.replace(reg, num);
        }
    }

    public void b() {

    }

    public static void h() {
        System.out.println("\n\nh = show help\n" +
                "d = dump register state\n" +
                "s = single step through the program (i.e. execute 1 instruction and stop)\n" +
                "s num = step through num instructions of the program\n" +
                "r = run until the program ends\n" +
                "m num1 num2 = display data memory from location num1 to num2\n" +
                "c = clear all registers, memory, and the program counter to 0\n" +
                "q = exit the program");
    }
    public void d() {
        System.out.println("\n\npc = " + pc);
        int count = 0;

        for ( Map.Entry<String, Integer> reg: regs.entrySet()) {
            if (reg.getKey() == "$zero") {
                continue;
            }
            System.out.print(reg.getKey() + " = " + reg.getValue());
            count++;
            if (count == 4) {
                System.out.print("\n");
                count = 0;
            }
            else {
                System.out.print("      ");
            }
        }
        System.out.print("\n");

    }

    public void s() {
        if (pc < insts.size()) {
            Instruction inst = insts.get(pc);
            switch (inst.getId()) {
                case ("add"):
                    int sum = regs.get(((R)inst).getRs()) + regs.get(((R)inst).getRt());
                    setReg(((R)inst).getRd(), sum);
                    pc++;
                    break;
                case ("and"):
                    int and = regs.get(((R)inst).getRs()) & regs.get(((R)inst).getRt());
                    setReg(((R)inst).getRd(), and);
                    pc++;
                    break;
                case ("or"):
                    int or = regs.get(((R)inst).getRs()) | regs.get(((R)inst).getRt());
                    setReg(((R)inst).getRd(), or);
                    pc++;
                    break;
                case ("sub"):
                    int sub = regs.get(((R)inst).getRs()) - regs.get(((R)inst).getRt());
                    setReg(((R)inst).getRd(), sub);
                    pc++;
                    break;
                case ("sll"):
                    int sll = regs.get(((R)inst).getRt()) << (((R)inst).getShamt());
                    setReg(((R)inst).getRd(), sll);
                    pc++;
                    break;
                case ("slt"):
                    int slt = regs.get(((R)inst).getRt()) - regs.get(((R)inst).getRs());
                    if (slt <= 0) {
                        slt = 0;
                    }
                    else {
                        slt = 1;
                    }
                    setReg(((R)inst).getRd(), slt);
                    pc++;
                    break;
                case ("jr"):
                    pc = regs.get(((R)inst).getRs());
                    break;
                case ("addi"):
                    int addisum = regs.get(((I)inst).getRs()) + ((I)inst).getImm();
                    setReg(((I)inst).getRt(), addisum);
                    pc++;
                    break;
                case ("lw"):
                    int lw = regs.get(((I)inst).getRs()) + ((I)inst).getImm();
                    setReg(((I)inst).getRt(), datamem[lw]);
                    pc++;
                    break;
                case ("sw"):
                    int sw = regs.get(((I)inst).getRs()) + ((I)inst).getImm();
                    datamem[sw] = regs.get(((I)inst).getRt());
                    pc++;
                    break;
                case ("beq"):
                    if (regs.get(((I)inst).getRs()) == regs.get(((I)inst).getRt())){
                        pc +=  1 + ((I)inst).getImm();
                        break;
                    }
                    else {
                        pc++;
                    }
                case ("bne"):
                    if (regs.get(((I)inst).getRs()) != regs.get(((I)inst).getRt())){
                        pc +=  1 + ((I)inst).getImm();
                    }
                    else {
                        pc++;
                    }
                    break;
                case ("j"):
                    pc = ((J)inst).getAddress();
                    break;
                case ("jal"):
                    regs.replace("$ra", pc+1);
                    pc = ((J)inst).getAddress();
                    break;
                default:
                    break;
            }

        }

    }

    public void snum(int numsteps) {
        int steps = 0;
        while (steps < numsteps) {
            s();
            steps++;
        }
        System.out.print("\n        " + numsteps + " instruction(s) executed");
    }

    public void r() {
        pc = 0;
        c();
        while (pc < insts.size()) {
            s();
        }
    }

    public void m(int start, int end) {
        System.out.println("\n");
        int spot = start;
        while (spot <= end) {
            System.out.println("[" + spot + "]" + " = " + datamem[spot]);
            spot++;
        }
    }

    public void c() {
        // set all registers to 0
        for (String reg : regs.keySet()) {
            regs.replace(reg, 0);
        }

        // clear memory
        for (int i = 0; i < 8192; i++) {
            datamem[i] = 0;
        }

        // set pc to 0
        pc = 0;
    }
}