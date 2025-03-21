package me.uyuyuy99.bbhack.rom;
import me.uyuyuy99.bbhack.MainMenu;
import me.uyuyuy99.bbhack.Info;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.yaml.snakeyaml.Yaml;
import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.lang.Exception;
import java.io.RandomAccessFile;

//equivalent of yamlSplit
public class ROMFiles {

    int INES_HEADER_SIZE = 0x10;
    int BANK_SIZE = 0x2000;
    String filePath = "/us.yaml";
    String outPath = "dumped/";

	private MainMenu main;

    public String name;
    public String md5;

    public static String bytesToMD5(byte[] inputBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(inputBytes);
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available.", e);
        }
    }

    //recursively delete directories
    public static void deleteDirectory(File file)
    {
        // store all the paths of files and folders present
        // inside directory
        if (file.listFiles() == null){
            return;
        }
        for (File subfile : file.listFiles()) {

            // if it is a subfolder,e.g Rohan and Ritik,
            //  recursively call function to empty subfolder
            if (subfile.isDirectory()) {
                deleteDirectory(subfile);
            }

            // delete files and empty subfolders
            subfile.delete();
        }
    }

    //recursively create directories
    public static void createDirectory(File file)
    {
        if (!file.exists()) {
            if (file.mkdirs()) {
                System.out.println("Folder created successfully.");
            } else {
                System.err.println("Failed to create folder.");
                return;
            }
        }
    }

	public ROMFiles(MainMenu instance) {
		main = instance;
        Yaml yaml = new Yaml();
        Map<String, Object> obj = yaml.load(Info.class.getResourceAsStream(filePath));

        //make sure rom is valid
        String md5 = obj.get("md5").toString();
        String getHash = bytesToMD5(main.rom.data);

        if (!md5.equals(getHash)){
            try {
                throw new Exception("Unsupported rom!");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        System.out.println("Dumping " + obj.get("name") + " for assets");

        File folder = new File(outPath);
        deleteDirectory(folder);
        folder.delete();
        createDirectory(folder);




        Map<String, Object> splits = (Map<String, Object>) obj.get("splits");


        int x = 0;
        boolean hasHeader = (boolean) obj.get("header");
        if (hasHeader){
            x += INES_HEADER_SIZE;
        }

        String[] sides = {"prg", "chr"};

        for (String side : sides){
            List<Map<String, Object>> banks = (List<Map<String, Object>>) splits.get(side);
            for (Map<String, Object> bank : banks){
                int bankid = (int) bank.get("bank");

                x = BANK_SIZE * bankid;
                if (hasHeader){
                    x += INES_HEADER_SIZE;
                }
                //add banksize based on other banks
                int myi = Arrays.asList(sides).indexOf(side);
                for (int i = 0; i < myi; i++){
                    List<Map<String, Object>> otherbanks = (List<Map<String, Object>>) splits.get(sides[i]);
                    x += BANK_SIZE * otherbanks.size();
                }

                int start = x;
                int end = x + BANK_SIZE;
                if (bank.containsKey("end")){
                    end = x + (int) bank.get("end");
                }

                byte[] bankdata = Arrays.copyOfRange(main.rom.data, start, end);

                //if bank exists but has no splits, just dump entire bank
                if (!bank.containsKey("splits")){
                    String filename = side+"/bank"+Integer.toHexString(bankid);

                    //support for directories
                    if (filename.indexOf("/") != -1){
                        List<String> nofile = new ArrayList<>(Arrays.asList(filename.split("/")));
                        nofile.remove(nofile.size()-1);
                        File newPath = new File(outPath+String.join("/", nofile));
                        createDirectory(newPath);
                    }

                    try {
                        RandomAccessFile rom = new RandomAccessFile(outPath+filename+".bin", "rw");
                        rom.write(bankdata);
                        rom.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    continue;
                }

                //do splits
                List<List<Object>> banksplits = (List<List<Object>>) bank.get("splits");
                for (List<Object> banksplit : banksplits){
                    int at = (int) banksplit.get(0);
                    String filename = side+"/bank"+Integer.toHexString(bankid)+"/unk"+Integer.toHexString(at);
                    if (banksplit.size() > 1){
                        filename = (String) banksplit.get(1);
                    }
                    System.out.println(at+" "+filename);

                    int split_start = at;
                    int split_end = BANK_SIZE;
                    if (bank.containsKey("end")){
                        split_end = (int) bank.get("end");
                    }

                    //get next split if exists
                    int split_i = banksplits.indexOf(banksplit);
                    if (split_i < banksplits.size()-1){
                        split_end = (int) banksplits.get(split_i+1).get(0);
                    }

                    byte[] filedata = Arrays.copyOfRange(bankdata, split_start, split_end);

                    //support for directories
                    if (filename.indexOf("/") != -1){
                        List<String> nofile = new ArrayList<>(Arrays.asList(filename.split("/")));
                        nofile.remove(nofile.size()-1);
                        File newPath = new File(outPath+String.join("/", nofile));
                        createDirectory(newPath);
                    }

                    try {
                        String usename = outPath+filename+".bin";
                        RandomAccessFile rom = new RandomAccessFile(usename, "rw");
                        //append
                        rom.seek(rom.length());
                        rom.write(filedata);
                        rom.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            }
        }

    }

}
