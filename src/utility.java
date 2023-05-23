import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class utility {

    public static LinkedHashMap<String, Integer> getRegList() {
        LinkedHashMap<String, Integer> reglist = new LinkedHashMap<>();
        reglist.put("$zero", 0);
        reglist.put("$0", 0);
        reglist.put("$v0", 2);
        reglist.put("$v1", 3);
        reglist.put("$a0", 4);
        reglist.put("$a1", 5);
        reglist.put("$a2", 6);
        reglist.put("$a3", 7);
        reglist.put("$t0", 8);
        reglist.put("$t1", 9);
        reglist.put("$t2", 10);
        reglist.put("$t3", 11);
        reglist.put("$t4", 12);
        reglist.put("$t5", 13);
        reglist.put("$t6", 14);
        reglist.put("$t7", 15);
        reglist.put("$s0", 16);
        reglist.put("$s1", 17);
        reglist.put("$s2", 18);
        reglist.put("$s3", 19);
        reglist.put("$s4", 20);
        reglist.put("$s5", 21);
        reglist.put("$s6", 22);
        reglist.put("$s7", 23);
        reglist.put("$t8", 24);
        reglist.put("$t9", 25);
        reglist.put("$sp", 29);
        reglist.put("$ra", 31);

        return reglist;
    }

    public static Hashtable<String, Integer> findLabels(File file) throws FileNotFoundException {
        Hashtable<String, Integer> labels = new Hashtable<>();
        Scanner scnr = new Scanner(file);
        int address = 0;
        while (scnr.hasNextLine()) {
            String[] line = scnr.nextLine().trim().split("\\s+");
            String firstchar = line[0].split("")[0];

            if (line.length == 0 || Objects.equals(line[0], "")) {
                continue;
            }
            if (Objects.equals(firstchar, "#")) {
                continue;
            }

            if (line[0].contains(":")) {
                String[] chars = line[0].split("");
                StringBuilder label = new StringBuilder();
                int k = 0;
                while (k < chars.length && !Objects.equals(chars[k], ":")) {
                    label.append(chars[k]);
                    k++;
                }

                labels.put(label.toString(), address);
                if ((chars.length > k+1 && !Objects.equals(chars[k+1], "#"))
                        || (line.length > 1 && !line[1].contains("#"))) {
                    address++;
                }
            }
            else {
                address++;
            }

        }
        return labels;
    }

    public static ArrayList<Instruction> getInstructions(File file, Hashtable<String, Integer> labels) throws FileNotFoundException {
        Scanner scnr = new Scanner(file);
        ArrayList<Instruction> instlist = new ArrayList<>();
        int address = 0;
        while (scnr.hasNextLine()) {
            String newline = scnr.nextLine().trim();

            if (newline.length() == 0) {
                continue;
            }

            String[] line = newline.split("");

            int comment_loc = line.length;
            int label_loc = -1;

            for (int i = 0; i < line.length; i++) {
                if (Objects.equals(line[i], "#")) {
                    comment_loc = i;
                    break;
                }
            }
            if (comment_loc == 0) {
                continue;
            }
            for (int i = 0; i < line.length; i++) {
                if (Objects.equals(line[i], ":")) {
                    label_loc = i;
                    break;
                }
            }

            if (comment_loc <= label_loc+1) {
                continue;
            }

            String[] insts = Arrays.copyOfRange(line, label_loc + 1, comment_loc);

            int j = 0;
            StringBuilder inst_builder = new StringBuilder();

            while (j < insts.length && !Objects.equals(insts[j], "$")){
                inst_builder.append(insts[j]);
                j++;
            }

            String inst_id = inst_builder.toString().trim();
            String label = "";
            if (inst_id.charAt(0) == 'j' && !inst_id.equals("jr")) {
                String[] inst_and_label = inst_id.split("\\s+");
                label = inst_and_label[1];
                inst_id = inst_and_label[0];
            }

            Instruction inst = switch (inst_id) {
                case "and" -> new R("and");
                case "add" -> new R("add");
                case "or" -> new R("or");
                case "sll" -> new R("sll");
                case "sub" -> new R("sub");
                case "slt" -> new R("slt");
                case "addi" -> new I("addi");
                case "beq" -> new I("beq");
                case "bne" -> new I("bne");
                case "lw" -> new I("lw");
                case "sw" -> new I("sw");
                case "j" -> new J("j");
                case "jr" -> new R("jr");
                case "jal" -> new J("jal");
                default -> new InvalidInstruction(inst_id);
            };

            if (inst instanceof R) {
                int k = j;

                if (Objects.equals(inst.getId(), "jr")) {
                    StringBuilder rs_builder = new StringBuilder();
                    while (k < insts.length && !Objects.equals(insts[k], ",")) {
                        rs_builder.append(insts[k]);
                        k++;
                    }
                    String rs = rs_builder.toString().trim();
                    ((R)inst).setRs(rs);
                }
                else {
                    StringBuilder rd_builder = new StringBuilder();
                    while (k < insts.length && !Objects.equals(insts[k], ",")) {
                        rd_builder.append(insts[k]);
                        k++;
                    }
                    String rd = rd_builder.toString().trim();
                    ((R)inst).setRd(rd);

                    k++;

                    if (!Objects.equals(inst.getId(), "sll")) {
                        StringBuilder rs_builder = new StringBuilder();
                        while (!Objects.equals(insts[k], ",")) {
                            rs_builder.append(insts[k]);
                            k++;
                        }
                        String rs = rs_builder.toString().trim();
                        ((R) inst).setRs(rs);
                        k++;

                        StringBuilder rt_builder = new StringBuilder();
                        while (k < insts.length && !Objects.equals(insts[k], "#")) {
                            rt_builder.append(insts[k]);
                            k++;
                        }
                        String rt = rt_builder.toString().trim();
                        ((R) inst).setRt(rt);
                    }
                    else {
                        StringBuilder rt_builder = new StringBuilder();
                        while (!Objects.equals(insts[k], ",")) {
                            rt_builder.append(insts[k]);
                            k++;
                        }
                        String rt = rt_builder.toString().trim();
                        ((R) inst).setRt(rt);
                        k++;
                        StringBuilder shamt_builder = new StringBuilder();
                        while (k < insts.length && !Objects.equals(insts[k], "#")) {
                            shamt_builder.append(insts[k]);
                            k++;
                        }
                        int shamt = Integer.parseInt(shamt_builder.toString().trim());
                        ((R)inst).setShamt(shamt);
                    }
                }
            }

            else if (inst instanceof J) {
                int label_address = labels.get(label);
                ((J)inst).setAddress(label_address);
            }

            else if (inst instanceof I) {
                int k = j;

                if (Objects.equals(inst.getId(), "sw") || Objects.equals(inst.getId(), "lw")) {

                    StringBuilder rt_builder = new StringBuilder();
                    while (!Objects.equals(insts[k], ",")) {
                        rt_builder.append(insts[k]);
                        k++;
                    }
                    String rt = rt_builder.toString().trim();
                    ((I)inst).setRt(rt);
                    k++;

                    StringBuilder imm_builder = new StringBuilder();
                    while (!Objects.equals(insts[k], "(")) {
                        imm_builder.append(insts[k]);
                        k++;
                    }
                    int imm = Integer.parseInt(imm_builder.toString().trim());
                    ((I)inst).setImm(imm);

                    k++;
                    StringBuilder rs_builder = new StringBuilder();
                    while (!Objects.equals(insts[k], ")")) {
                        rs_builder.append(insts[k]);
                        k++;
                    }
                    String rs = rs_builder.toString().trim();
                    ((I)inst).setRs(rs);
                }

                else {

                    if (Objects.equals(inst.getId(), "addi")) {
                        StringBuilder rt_builder = new StringBuilder();
                        while (!Objects.equals(insts[k], ",")) {
                            rt_builder.append(insts[k]);
                            k++;
                        }
                        String rt = rt_builder.toString().trim();
                        ((I)inst).setRt(rt);
                        k++;
                        StringBuilder rs_builder = new StringBuilder();
                        while (!Objects.equals(insts[k], ",")) {
                            rs_builder.append(insts[k]);
                            k++;
                        }
                        String rs = rs_builder.toString().trim();
                        ((I)inst).setRs(rs);
                        k++;
                        StringBuilder imm_builder = new StringBuilder();
                        while (k < insts.length && !Objects.equals(insts[k], "#")) {
                            imm_builder.append(insts[k]);
                            k++;
                        }
                        int imm = Integer.parseInt(imm_builder.toString().trim());
                        ((I) inst).setImm(imm);
                    }
                    else {
                        StringBuilder rs_builder = new StringBuilder();
                        while (!Objects.equals(insts[k], ",")) {
                            rs_builder.append(insts[k]);
                            k++;
                        }
                        String rs = rs_builder.toString().trim();
                        ((I)inst).setRs(rs);
                        k++;
                        StringBuilder rt_builder = new StringBuilder();
                        while (!Objects.equals(insts[k], ",")) {
                            rt_builder.append(insts[k]);
                            k++;
                        }
                        String rt = rt_builder.toString().trim();
                        ((I)inst).setRt(rt);
                        k++;
                        StringBuilder label_builder = new StringBuilder();
                        while (k < insts.length && !Objects.equals(insts[k], "#")) {
                            label_builder.append(insts[k]);
                            k++;
                        }
                        String branch_label = label_builder.toString().trim();
                        int label_address = labels.get(branch_label);
                        ((I)inst).setImm(label_address - (address + 1));

                    }
                }
            }
            else {
                instlist.add(inst);
                break;
            }

            instlist.add(inst);
            address++;
        }
        return instlist;
    }

    public static String signExtend(String bin_input, int num_bits) {
        if (bin_input.length() < num_bits) {
            StringBuilder bin_output = new StringBuilder(bin_input);
            while (bin_output.length() < num_bits) {
                bin_output.insert(0, 0);
            }
            return bin_output.toString();
        }
        else {
            return bin_input;
        }
    }

    public static String signReduce(String bin_input, int num_bits) {
        if (bin_input.length() > num_bits) {
            StringBuilder bin_output = new StringBuilder(bin_input);
            bin_output.delete(0, bin_input.length()-num_bits);
            return bin_output.toString();
        }
        else {
            return bin_input;
        }
    }

}