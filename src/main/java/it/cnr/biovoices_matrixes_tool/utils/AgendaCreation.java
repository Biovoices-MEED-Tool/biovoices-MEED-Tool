package it.cnr.biovoices_matrixes_tool.utils;

import com.aspose.words.*;
import com.aspose.words.Font;
import com.aspose.words.Shape;

import java.awt.*;
import java.io.FileInputStream;

public class AgendaCreation {

    private static String serverPath = "";
    public static String licenseServerPath = "";
    public static String imageServerPath = "";

    public static String localPath = "";
    public static String licenseLocalPath = "";
    public static String imageLocalPath = "";

    public static String createAgenda(String[][] rows) throws Exception {
        FileInputStream fstream = new FileInputStream(licenseServerPath);

        License license = new License();
        license.setLicense(fstream);
        Document doc = new Document();
        DocumentBuilder builder = new DocumentBuilder(doc);

        Shape shape = builder.insertImage(imageServerPath + "insertLogoHere.png");
        shape.setWrapType(WrapType.TOP_BOTTOM);
        shape.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        builder.write(ControlChar.LINE_BREAK);

        builder.getFont().setName("Arial");
        builder.getFont().setSize(26);
        builder.getFont().setColor(new Color (43, 150, 78));
        builder.getFont().setBold(true);

        builder.write("Type here the event's title" + ControlChar.LINE_BREAK);

        builder.getFont().setName("Arial");
        builder.getFont().setSize(14);
        builder.getFont().setColor(new Color (173, 173, 173));
        builder.getFont().setBold(false);
        builder.write( ControlChar.LINE_BREAK + "Type here the event's place and date" + ControlChar.LINE_BREAK + ControlChar.LINE_BREAK);

        Table table = builder.startTable();

        String[] heading = rows[0];

        // Make the header row.
        builder.insertCell();
        // Set the left indent for the table. Table wide formatting must be applied after
        // at least one row is present in the table.
        table.setLeftIndent(20.0);

        // Set height and define the height rule for the header row.
        builder.getRowFormat().setHeight(40.0);
        builder.getRowFormat().setHeightRule(HeightRule.AT_LEAST);
        // Some special features for the header row.
        builder.getCellFormat().getShading().setBackgroundPatternColor(new Color (43, 150, 78));
        builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        builder.getFont().setSize(11);
        builder.getFont().setName("Arial");
        builder.getFont().setColor(Color.white);
        builder.getFont().setBold(true);

        builder.getCellFormat().setWidth(100.0);
        builder.write(heading[0]);

        // We don't need to specify the width of this cell because it's inherited from the previous cell.
        builder.insertCell();
        builder.write(heading[1]);

        builder.insertCell();
        builder.write(heading[2]);

        builder.insertCell();
        builder.write(heading[3]);
        builder.endRow();

        for(int i = 1; i < rows.length; i++) {
            if(rows[i].length == 2) {
                if(!rows[i][1].equals("Drag an activity") && !rows[i][1].equals("Drag an activity")) {
                    if(rows[i][1].equals("Insert time")) {
                        builder.getCellFormat().getShading().setBackgroundPatternColor(new Color(43, 150, 78, 50));
                        builder.getFont().setColor(Color.WHITE);
                        builder.getFont().setBold(false);
                        builder.insertCell();

                        builder.getCellFormat().setHorizontalMerge(CellMerge.FIRST);
                        builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
                        builder.write(rows[i][0]);

                        builder.insertCell();
                        builder.getCellFormat().setHorizontalMerge(CellMerge.PREVIOUS);

                        builder.insertCell();
                        builder.getCellFormat().setHorizontalMerge(CellMerge.FIRST);
                        builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
                        builder.write(rows[i][1]);
                        builder.insertCell();
                        builder.getCellFormat().setHorizontalMerge(CellMerge.PREVIOUS);
                    }
                    else {
                        builder.getCellFormat().getShading().setBackgroundPatternColor(Color.white);
                        builder.getCellFormat().setHorizontalMerge(CellMerge.NONE);
                        builder.getFont().setColor(Color.black);
                        builder.getFont().setBold(false);

                        builder.insertCell();
                        builder.insertCell();
                        builder.insertCell();
                        builder.write(rows[i][0]);

                        builder.insertCell();
                        builder.write(rows[i][1]);
                    }
                    builder.endRow();
                }
            }
            if(rows[i].length == 4) {
                for (int j = 0; j < rows[i].length; j++) {
                    builder.getCellFormat().getShading().setBackgroundPatternColor(Color.white);
                    builder.getFont().setColor(Color.black);
                    builder.getFont().setBold(false);
                    builder.getCellFormat().setHorizontalMerge(CellMerge.NONE);
                    builder.insertCell();
                    builder.write(rows[i][j].replace("*$", ","));
                }
                builder.endRow();
            }
        }

        builder.endTable();
        table.setAlignment(TableAlignment.CENTER);
        doc.save(serverPath + "agenda.doc");

        return serverPath + "agenda.doc";
    }

}
