import java.io.*;
import java.util.*;

public class MacroPass2 {
    public static void main(String[] args) throws Exception {
        BufferedReader irb = new BufferedReader(new FileReader("intermediate.txt"));
        BufferedReader mdtb = new BufferedReader(new FileReader("mdt.txt"));
        BufferedReader mntb = new BufferedReader(new FileReader("mnt.txt"));
        PrintWriter pw = new PrintWriter("pass2.txt");

        ArrayList<String> mdt = new ArrayList<>();
        ArrayList<String[]> mnt = new ArrayList<>();
        ArrayList<String> mntNames = new ArrayList<>();

        String line;

        // ---------- READ MDT ----------
        while ((line = mdtb.readLine()) != null) {
            if (line.trim().isEmpty())
                continue; // Skip blank lines
            mdt.add(line.trim());
        }

        // ---------- READ MNT ----------
        boolean headerSkipped = false;
        while ((line = mntb.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty())
                continue;
            // skip header line such as "Index Macro Name"
            if (!headerSkipped && 
                (line.toLowerCase().contains("index") || line.toLowerCase().contains("macro"))) {
                headerSkipped = true;
                continue;
            }
            String[] parts = line.split("\\s+");
            if (parts.length >= 2) {
                mnt.add(parts);
                mntNames.add(parts[1]); // Macro Name is in column 2
            }
        }

        // ---------- PROCESS INTERMEDIATE FILE ----------
        while ((line = irb.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");

            if (parts.length > 0 && mntNames.contains(parts[0])) {
                int mntIndex = mntNames.indexOf(parts[0]);
                String[] mntEntry = mnt.get(mntIndex);

                int mdtp = mntIndex + 1; // since MDT lines match macro order, and MDT format is "line_no macro_expansion"
                int numParams = parts.length - 1;

                // Build APTAB (argument table for macro call)
                String[] aptab = new String[20];
                for (int i = 0; i < numParams; i++) {
                    aptab[i + 1] = parts[i + 1].replace(",", "");
                }

                // Expand macro from MDT
                int i = mdtp; // MDT line, skipping header
                while (i < mdt.size()) {
                    String mdtLine = mdt.get(i);

                    if (mdtLine.toUpperCase().contains("MEND"))
                        break;

                    String[] tokens = mdtLine.split("\\s+");
                    StringBuilder outputLine = new StringBuilder("+");

                    for (String token : tokens) {
                        if (token.contains("(P,")) {
                            int idx = Integer.parseInt(token.replaceAll("[^0-9]", ""));
                            outputLine.append(aptab[idx]).append("\t");
                        } else {
                            outputLine.append(token).append("\t");
                        }
                    }

                    pw.println(outputLine.toString().trim());
                    System.out.println(outputLine.toString().trim());
                    i++;
                }
            } else {
                pw.println(line);
                System.out.println(line);
            }
        }

        pw.close();
        mntb.close();
        mdtb.close();
        irb.close();

        System.out.println("\n--- END OF OUTPUT ---");
        System.out.println("Pass 2 of Macro Assembler executed successfully.");
    }
}
