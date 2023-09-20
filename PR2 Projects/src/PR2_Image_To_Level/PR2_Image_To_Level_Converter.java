package PR2_Image_To_Level;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts 2D binary image into level data.
 */
public class PR2_Image_To_Level_Converter {
    private static List<List<Integer>> matrix = new ArrayList<>();

    private static String blocktype = "101";

    public static void main(String[] args) throws Exception {
        createMatrix();
        convertToLevel();
    }

    private static void convertToLevel() {
        StringBuilder builder = new StringBuilder();
        int xshift = 1;
        int yshift = 0;
        int blocks = 0;
        for (List<Integer> aMatrix : matrix) {
            for (int x = 0; x < matrix.get(0).size(); x++) {
                if (aMatrix.get(x) == 0) {
                    builder.append(",").append(xshift).append(";").append(yshift);
                    if (!blocktype.equals("")) {
                        builder.append(";").append(blocktype);
                        blocktype = "";
                    }
                    yshift = 0;
                    xshift = 1;
                    blocks++;
                } else {
                    xshift++;
                }
            }
            xshift -= matrix.get(0).size();
            yshift++;
        }
        System.out.println(builder.toString());
        System.out.println("\nBlocks: " + blocks);
    }

    private static void createMatrix() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("textfiles/image.txt"));
        String line;
        while ((line = br.readLine()) != null) {
            List<Integer> bits = new ArrayList<>();
            for (Character c : line.toCharArray()) bits.add(c == '0' ? 0 : 1);
            matrix.add(bits);
        }
    }


}
