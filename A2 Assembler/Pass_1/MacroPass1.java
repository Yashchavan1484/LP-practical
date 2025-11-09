import java.io.*;
import java.util.*;

public class MacroPass1 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("macro_input.asm"));
        PrintWriter mnt = new PrintWriter("mnt.txt");
        PrintWriter mdt = new PrintWriter("mdt.txt");
        PrintWriter ir = new PrintWriter("intermediate.txt");

        mnt.println("Index\tMacro Name"); // Header as required

        int macroIndex = 1;
        ArrayList<String> pntab = new ArrayList<>();
        String line;
        boolean inMacro = false;
        int mdtLineNo = 1;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty())
                continue;
            String[] parts = line.split("\\s+");

            if (parts[0].equalsIgnoreCase("MACRO")) {
                inMacro = true;
                line = br.readLine();
                parts = line.trim().split("\\s+");
                String macroName = parts[0];
                mnt.println(macroIndex + "\t" + macroName);
                macroIndex++;
                // Build PNTAB for this macro
                pntab.clear();
                for (int i = 1; i < parts.length; i++) {
                    String param = parts[i].replace("&", "").replace(",", "");
                    // Avoid empty params from trailing spaces/commas
                    if (!param.isEmpty()) pntab.add(param);
                }
            } else if (inMacro && parts[0].equalsIgnoreCase("MEND")) {
                mdt.println(mdtLineNo + "\tMEND");
                mdtLineNo++;
                inMacro = false;
                pntab.clear();
            } else if (inMacro) {
                mdt.print(mdtLineNo + "\t");
                for (String token : parts) {
                    if (token.contains("&")) {
                        // Replace macro parameter with (P,x)
                        String param = token.replace("&", "").replace(",", "");
                        int idx = pntab.indexOf(param) + 1; // 1-based index
                        mdt.print("(P," + idx + ") ");
                    } else {
                        mdt.print(token + " ");
                    }
                }
                mdt.println();
                mdtLineNo++;
            } else {
                ir.println(line);
            }
        }

        br.close();
        mnt.close();
        mdt.close();
        ir.close();
    }
}
