import java.text.DecimalFormat;
import java.util.*;

public class MipsSimulator {
    private final int[] datamem = new int[8192];
    private int pc = 0;
    private final int ghr_size;
    private int ghr = 0;
    private int tot_predictions = 0;
    private int acc_predictions = 0;
    private final int[] predictors;
    private final LinkedHashMap<String, Integer> regs = new LinkedHashMap<>();
    private final ArrayList<Instruction> insts;

    public MipsSimulator(ArrayList<Instruction> insts, int ghr_size) {

        // set all registers to hold 0
        for (String reg : utility.getRegList().keySet()) {
            regs.put(reg, 0);
        }

        this.insts = insts;
        this.ghr_size = ghr_size;
        int pred_size = (int) Math.pow(2, ghr_size);
        predictors = new int[pred_size];

        Arrays.fill(predictors, 0);
    }

    public void setReg(String reg, int num) {
        if (!Objects.equals(reg, "$0") && !Objects.equals(reg, "$zero")) {
            regs.replace(reg, num);
        }
    }

    public void b() {
        DecimalFormat df = new DecimalFormat("0.00");
        String acc = df.format(((float)acc_predictions/(float)tot_predictions) * 100);
        System.out.println("\n\naccuracy " + acc + "% (" +
                acc_predictions + " correct predictions, " + tot_predictions + " predictions)");
    }

    public static void h() {
        System.out.println("""


                h = show help
                d = dump register state
                s = single step through the program (i.e. execute 1 instruction and stop)
                s num = step through num instructions of the program
                r = run until the program ends
                m num1 num2 = display data memory from location num1 to num2
                c = clear all registers, memory, and the program counter to 0
                q = exit the program""");
    }
    public void d() {
        System.out.println("\n\npc = " + pc);
        int count = 0;

        for ( Map.Entry<String, Integer> reg: regs.entrySet()) {
            if (Objects.equals(reg.getKey(), "$zero")) {
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
            boolean branchTaken = false;
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
                    if (Objects.equals(regs.get(((I) inst).getRs()), regs.get(((I) inst).getRt()))){
                        pc +=  1 + ((I)inst).getImm();
                        branchTaken = true;
                    }
                    else {
                        pc++;
                    }
                    break;
                case ("bne"):
                    if (!Objects.equals(regs.get(((I) inst).getRs()), regs.get(((I) inst).getRt()))){
                        pc +=  1 + ((I)inst).getImm();
                        branchTaken = true;
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

            // determine if prediction was accurate
            if (Objects.equals(inst.getId(), "beq") || Objects.equals(inst.getId(), "bne")) {
                int prev_prediction = predictors[ghr];
                if (branchTaken) {
                    if (prev_prediction == 2 || prev_prediction == 3) {
                        acc_predictions++;
                    }
                    tot_predictions++;

                    // update predictors
                    if (prev_prediction != 3) {
                        predictors[ghr] = prev_prediction + 1;
                    }

                    // update ghr
                    ghr = ((ghr << 1) & (~(int)Math.pow(2, ghr_size))) + 1;
                }
                else {
                    if (prev_prediction == 0 || prev_prediction == 1) {
                        acc_predictions++;
                    }
                    tot_predictions++;

                    // update predictors
                    if (prev_prediction != 0){
                        predictors[ghr] = prev_prediction - 1;
                    }

                    // update ghr
                    ghr = (ghr << 1) & (~(int)Math.pow(2, ghr_size));
                }
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
