/*
 * Copyright (c) 2023. PortSwigger Ltd. All rights reserved.
 *
 * This code may be used to extend the functionality of Burp Suite Community Edition
 * and Burp Suite Professional, provided that this usage does not violate the
 * license terms for those products.
 */

package example.contextmenu;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyContextMenuItemsProvider implements ContextMenuItemsProvider
{

    private final MontoyaApi api;

    public MyContextMenuItemsProvider(MontoyaApi api)
    {
        this.api = api;
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event)
    {
        if (event.isFromTool(ToolType.PROXY, ToolType.TARGET, ToolType.REPEATER, ToolType.EXTENSIONS))
        {
            List<Component> menuItemList = new ArrayList<>();

            JMenuItem saveResponseBodyItem = new JMenuItem("To file");

            HttpRequestResponse requestResponse = event.messageEditorRequestResponse().isPresent() ? event.messageEditorRequestResponse().get().requestResponse() : event.selectedRequestResponses().get(0);

            if (requestResponse.response() != null)
            {
                saveResponseBodyItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        JFileChooser fileChooser = new JFileChooser();

                        // Show the file chooser dialog
                        int result = fileChooser.showSaveDialog(null);

                        // Check if the user selected a file
                        if (result == JFileChooser.APPROVE_OPTION) {
                            // Get the selected file
                            File selectedFile = fileChooser.getSelectedFile();

                            try (FileOutputStream writer = new FileOutputStream(selectedFile)) {
                                writer.write(requestResponse.response().body().getBytes());
                                api.logging().logToOutput("response save ok:\r\n" +selectedFile.getAbsolutePath()+" || response save ok:\r\n");
                            
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                                api.logging().logToError("save error");                               
                            }
                            // Update the label with the selected file's path
                            //selectedFileLabel.setText("Selected File: " + selectedFile.getAbsolutePath());
                        }
                    }
                });
                menuItemList.add(saveResponseBodyItem);
            }

            return menuItemList;
        }

        return null;
    }
}
