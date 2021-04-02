package ntu.mdpg1app;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ntu.mdpg1app.bluetooth.BluetoothChatFragment;
import ntu.mdpg1app.model.HexBin;
import ntu.mdpg1app.model.IDblock;
import ntu.mdpg1app.model.Map;
import ntu.mdpg1app.model.Position;
import ntu.mdpg1app.model.Robot;
import ntu.mdpg1app.model.WayPoint;

public class MainActivity extends AppCompatActivity {
    GridLayout base_layout;
    BluetoothChatFragment fragment;
    MenuItem menu_set_robot_position;
    MenuItem menu_set_waypoint;
    MenuItem menu_remove_waypoint;

    MenuItem menu_update_map;
    MenuItem menu_auto_update_map;
    MenuItem menu_set_config1;
    MenuItem menu_set_config2;
    MenuItem menu_enable_swipe_input;
    MenuItem menu_show_bluetooth_chat;
    MenuItem menu_show_data_strings;
    MenuItem menu_calibrate;

    TextView tv_status;
    Button btn_forward;
    Button btn_left;
    Button btn_right;
    Button btn_reset;
    Button btn_terminate;
    Button btn_explr;
    Button btn_fastest;
    Button btn_config1;
    Button btn_config2;

    String robot_status = "Idle";
    String dir = "NORTH";
    String xCoor = "1";
    String yCoor = "1";
    String p1 = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
    String p2 = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize all the buttons
        tv_status = (TextView) findViewById(R.id.tv_status);
        btn_forward = (Button) findViewById(R.id.btn_forward);
        btn_left= (Button) findViewById(R.id.btn_left);
        btn_right= (Button) findViewById(R.id.btn_right);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_terminate= (Button) findViewById(R.id.btn_terminate);
        btn_explr= (Button) findViewById(R.id.btn_explr);
        btn_fastest= (Button) findViewById(R.id.btn_fastest);
        btn_config1= (Button) findViewById(R.id.btn_config1);
        btn_config2= (Button) findViewById(R.id.btn_config2);
        base_layout = (GridLayout) findViewById(R.id.base_layout);

        //initialize the bluetooth service component
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment = new BluetoothChatFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }

        //initialize default status "idle"
        updateStatus(robot_status);

        //initialize listener for all the buttons
        setBtnListener();

        //initialize the grid
        loadGrid();

        //Query RPI to get robot info
        queryMessage();
    }

    public void queryMessage(){
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(fragment != null) {
                    if(fragment.getChatService() != null){
                        if(fragment.getChatServiceState() == 3){
                            Log.d("Debug", "getChatServiceState = STATE_CONNECTED");
                            outgoingMessage("GetRobotInfo\\{};", true);
                        }
                    }
                }
            }
        }, 0, 1000);
    }

    //method to update the label textview
    public void updateStatus(String status){
        this.robot_status = status;
        tv_status.setText(status);
    }

    //method to set all the event for buttons
    private void setBtnListener(){
        btn_forward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //check if robot is out of bounds
                if(!Robot.getInstance().isOutOfBounds()) {
                    Robot.getInstance().moveForward(10);
//                    outgoingMessage("W", 1);
//                    loadGrid();

                    try {
                        String instructionValue = new JSONObject().put("step", 1).toString();
                        String instruction = "Forward\\" + instructionValue + ";";
                        outgoingMessage(instruction, false);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                updateStatus("Forward");
            }
        });

        btn_left.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Robot.getInstance().rotateLeft();
//                outgoingMessage("A", 1);
//                loadGrid();

                try {
                    String instructionValue = new JSONObject().put("angle", 90).toString();
                    String instruction = "TurnLeft\\" + instructionValue + ";";
                    outgoingMessage(instruction, false);
                    updateStatus("Turn Left");

                } catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });

        btn_right.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Robot.getInstance().rotateRight();
//                outgoingMessage("D", 1);
//                loadGrid();

                try {
                    String instructionValue = new JSONObject().put("angle", 90).toString();
                    String instruction = "TurnRight\\" + instructionValue + ";";
                    outgoingMessage(instruction, false);
                    updateStatus("Turn Right");

                } catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String instruction = "Reset\\{};";
                Robot r = Robot.getInstance();
                r.setPosX(1);
                r.setPosY(1);
                r.setDirection("NORTH");
                WayPoint.getInstance().setPosition(null);
                p2 = "0000000000000000000000000000000000000000000000000000000000000000000000000000";
                Map.getInstance().setMapJson(p2);
                if (menu_auto_update_map != null && menu_auto_update_map.isChecked()) {
                    loadGrid();
                }
                outgoingMessage(instruction, false);
                Log.d("Debug", "reset fp pressed");
                updateStatus(STATUS_RESET_FP);
            }
        });

        btn_terminate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String instruction = "Terminate\\{};";
                outgoingMessage(instruction, false);
//                outgoingMessage("X", 0);
                updateStatus(STATUS_TERMINATE_DESC);
            }
        });

        btn_explr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String instructionValue = new JSONObject().put("mode", 0).toString();
                    String instruction = "SetRobotMode\\" + instructionValue + ";";
                    outgoingMessage(instruction, false);
                } catch(JSONException e){
                    e.printStackTrace();
                }

//                outgoingMessage("BEGINEX", 0);
                updateStatus(STATUS_EX_DESC);
            }
        });
        btn_fastest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (WayPoint.wp.getPosition() == null) {
                    robot_status = "Setting WayPoint";
                    updateStatus(robot_status);

                    menu_set_robot_position.setChecked(false);
                    menu_set_waypoint.setChecked(true);
                    Toast toast=Toast.makeText(getApplicationContext(),"Tap the Grid to set WayPoint",Toast.LENGTH_LONG);
                    toast.show();

                }else{
                    try {
                        String instructionValue = new JSONObject().put("mode", 1).toString();
                        String instruction = "SetRobotMode\\" + instructionValue + ";";
                        outgoingMessage(instruction, false);
                    }catch(JSONException e) {
                        e.printStackTrace();
                    }

//                    outgoingMessage("BEGINFP", 0);
                    updateStatus(STATUS_FP_DESC);
                }
            }
        });

        btn_config1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
                String retrievedText = prefs.getString("string1", null);
                if (retrievedText != null) {
                    outgoingMessage(retrievedText, false);
                }
            }
        });

        btn_config2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
                String retrievedText = prefs.getString("string2", null);
                if (retrievedText != null) {
                    outgoingMessage(retrievedText, false);
                }
            }
        });
    }

    //initalize all the menu item button
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu_set_robot_position = menu.findItem(R.id.action_set_robot_position);
        menu_set_waypoint = menu.findItem(R.id.action_set_waypoint);
        menu_remove_waypoint = menu.findItem(R.id.action_remove_waypoint);
        menu_update_map = menu.findItem(R.id.action_update_map);
        menu_auto_update_map = menu.findItem(R.id.action_auto_update_map);
        menu_set_config1 = menu.findItem(R.id.action_set_config_string1);
        menu_set_config2 = menu.findItem(R.id.action_set_config_string2);
        menu_show_data_strings = menu.findItem(R.id.action_view_data_strings);
        menu_enable_swipe_input = menu.findItem(R.id.action_enable_swipe_input);
        menu_show_bluetooth_chat = menu.findItem(R.id.action_show_bluetooth_chat);
        return true;
    }

    //load/reload the Grid View into the page
    private void loadGrid(){
        MapCanvas mCustomDrawableView = new MapCanvas(this);
        //set touch event and swipe event
        if(menu_enable_swipe_input!=null&&menu_enable_swipe_input.isChecked()){
            mCustomDrawableView.setGesture(this);
        }else{
            mCustomDrawableView.setOnTouchListener(mCustomDrawableView);
        }
        base_layout.removeAllViews();
        base_layout.addView(mCustomDrawableView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //this is the event when menu item is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        if (id == R.id.action_set_robot_position) {
            boolean checked = item.isChecked();
            clearAllEditableCheckbox();
            item.setChecked(!checked);
            return true;
        }
        if (id == R.id.action_set_waypoint) {
            boolean checked = item.isChecked();
            clearAllEditableCheckbox();
            item.setChecked(!checked);
            return true;
        }
        if (id == R.id.action_remove_waypoint) {
            removeWaypoint();
            return true;
        }
        if (id == R.id.action_update_map) {
            loadGrid();
            return true;
        }
        if (id == R.id.action_auto_update_map) {
            boolean checked = item.isChecked();
            item.setChecked(!checked);
            if(item.isChecked()){
                loadGrid();
            }
            return true;
        }
        if (id == R.id.action_set_config_string1) {
            setConfiguredString(1);
            return true;
        }
        if (id == R.id.action_set_config_string2) {
            setConfiguredString(2);
            return true;
        }

        if (id == R.id.action_view_data_strings) {
            displayDataStrings();
            return true;
        }

        if (id == R.id.action_enable_swipe_input) {
            boolean checked = item.isChecked();
            clearAllEditableCheckbox();
            item.setChecked(!checked);
            loadGrid();
            return true;
        }

        if (id == R.id.action_show_bluetooth_chat) {
            boolean checked = item.isChecked();
            item.setChecked(!checked);
            if(fragment!=null){
                fragment.showChat(!checked);
            }
            return true;
        }

        if(id == R.id.action_calibrate) {
            calibrateRobot();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //this method opens up a dialog to save input string on the phone
    //index is the key of the string.
    //eg string 1 is stored in 'string1', string 2 in 'string2'
    private void setConfiguredString(final int index){
        final EditText txtField = new EditText(this);
        SharedPreferences prefs = getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
        String retrievedText = prefs.getString("string"+index, null);
        if (retrievedText != null) {
            txtField.setText(retrievedText);
        }

        new AlertDialog.Builder(this)
                .setTitle("Configure String "+index)
                .setView(txtField)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String input = txtField.getText().toString();
                        SharedPreferences.Editor editor = getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE).edit();
                        editor.putString("string"+index, input);
                        editor.apply();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    private void displayDataStrings(){

        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("MDF + Image String");

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.data_strings, null);
        builder.setView(customLayout);

        // add a button
        builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                dialog.dismiss();
            }
        });

        // set the custom dialog components - text, image and button
        EditText text_mdf1 = (EditText) customLayout.findViewById(R.id.textbox_mdf1);
        String explored = HexBin.binToHex(Map.getInstance().getBinaryExplored());
        text_mdf1.setText(explored);

        EditText text_mdf2 = (EditText) customLayout.findViewById(R.id.textbox_mdf2);
        String obstacles = HexBin.binToHex(Map.getInstance().getBinaryExploredObstacle());
        //obstacles = obstacles + ";" + Map.getInstance().getObstacleStringFromAlgo();
        text_mdf2.setText(obstacles);

        EditText text_imgreg = (EditText) customLayout.findViewById(R.id.textbox_imgreg);
        String imgreg = "{";
        ArrayList<IDblock> numberedBlocks = Map.getInstance().getNumberedBlocks();
        for(IDblock blk : numberedBlocks){
            String imgID;
            switch (blk.getID()){
                case "WHITE_UP":
                    imgID = "1";
                    break;
                case "BLUE_DOWN":
                    imgID = "2";
                    break;
                case "YELLOW_RIGHT":
                    imgID = "3";
                    break;
                case "RED_LEFT":
                    imgID = "4";
                    break;
                case "GREEN_CIRCLE":
                    imgID = "5";
                    break;
                case "BLUE_SIX":
                    imgID = "6";
                    break;
                case "GREEN_SEVEN":
                    imgID = "7";
                    break;
                case "RED_EIGHT":
                    imgID = "8";
                    break;
                case "WHITE_NINE":
                    imgID = "9";
                    break;
                case "YELLOW_ZERO":
                    imgID = "10";
                    break;
                case "RED_V":
                    imgID = "11";
                    break;
                case "GREEN_W":
                    imgID = "12";
                    break;
                case "WHITE_X":
                    imgID = "13";
                    break;
                case "BLUE_Y":
                    imgID = "14";
                    break;
                case "YELLOW_Z":
                    imgID = "15";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + blk.getID());
            }
            imgreg += String.format("(%s, %d, %d)", imgID, blk.getPosition().getPosX(), blk.getPosition().getPosY());
            imgreg += ", ";
        }
        if(imgreg.length()>2)imgreg = imgreg.substring(0, imgreg.length()-2);
        imgreg += "}";
        text_imgreg.setText(imgreg);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //this is to uncheck all the checkable menuitem
    private void clearAllEditableCheckbox(){
        menu_set_robot_position.setChecked(false);
        menu_set_waypoint.setChecked(false);
        menu_enable_swipe_input.setChecked(false);
    }

    public static final String STATUS_EX_DESC = "Moving (Exploring)";
    public static final String STATUS_FP_DESC = "Moving (Fastest Path)";
    public static final String STATUS_TERMINATE_DESC = "Terminated";
    public static final String STATUS_RESET_FP = "Reset FP";

    //method is ran when new message comes in
    @SuppressLint("LongLogTag")
    public void incomingMessage(String readMsg) {
        //update map
        final Robot r = Robot.getInstance();

        Log.d("Read Message", "Message = " + readMsg);

        if(readMsg.length()>0) {
            menu_show_bluetooth_chat.setChecked(true);
            fragment.showChat(true);

            JSONObject jsonMsg, robotPos, mapInfo;
            JSONArray images = null;

            System.out.println(readMsg);

            try {
                jsonMsg = new JSONObject(readMsg);

                if(jsonMsg.has("pos")) {
                    robotPos = (JSONObject) jsonMsg.get("pos");
                    if (!robotPos.has("dir")) {
                        dir = "NORTH";
                    } else {
                        dir = robotPos.get("dir").toString();
                    }
                    xCoor = robotPos.get("x").toString();
                    yCoor = robotPos.get("y").toString();
                }

                if(jsonMsg.has("map")) {
                    mapInfo = (JSONObject) jsonMsg.get("map");
                    p1 = mapInfo.get("p1").toString();
                    p2 = mapInfo.get("p2").toString();
                    Log.d("Debug", "p2 = " + p2);
                }else{
                    p1 = p1;
                    p2 = p2;
                }

                if(jsonMsg.has("images")) {
                    images = jsonMsg.getJSONArray("images");
                }

                if(jsonMsg.has("robot_status"))
                    robot_status = jsonMsg.get("robot_status").toString();
                else{
                    robot_status = robot_status;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("TESTE", "Cannot turn string msg to json");
            }

            /**
             * RPI send map descriptor
             * formerly GRID command
             */
            if(p2.length()>0){
                Map.getInstance().setMapJson(p2);
                if (menu_auto_update_map != null && menu_auto_update_map.isChecked()) {
                    loadGrid();
                }
            }

            /**
             * RPI send robot position
             * formerly ROBOTPOSITION command
             */
            if(dir.length()>0 && xCoor.length()>0 && yCoor.length()>0){
                r.setPosX(Float.parseFloat(xCoor));
                r.setPosY(Float.parseFloat(yCoor));
                r.setDirection(dir);

                if (menu_auto_update_map != null && menu_auto_update_map.isChecked()) {
                    loadGrid();
                }
            }

            /**
             * RPI send map descriptor + robot position
             * formerly DATA command
             */
            if(p1.length()!=0 && p2.length()!=0 && dir.length()!=0 && xCoor.length()!=0 && yCoor.length()!=0){
                //map descriptor and robot position values are not empty

                Map.getInstance().setMap(p1, "", p2);

                r.setPosX(Float.parseFloat(xCoor));
                r.setPosY(Float.parseFloat(yCoor));
                r.setDirection(dir);

                if (menu_auto_update_map != null && menu_auto_update_map.isChecked()) {
                    loadGrid();
                }
            }

            /**
             * RPI send image ID
             * formerly BLOCK command
             */
            if(images != null){
                ArrayList<IDblock> updatedList = new ArrayList<IDblock>();
                for (int i=0; i<images.length(); i++) {
                    JSONObject tempImg;
                    try {
                        tempImg = images.getJSONObject(i);
                        String imageID = tempImg.get("id").toString();
                        String imageX = tempImg.get("x").toString();
                        String imageY = tempImg.get("y").toString();

                        IDblock input = new IDblock(imageID, Integer.parseInt(imageX), Integer.parseInt(imageY));
                        updatedList.add(input);
//                        Map.getInstance().addNumberedBlocks(input);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Map.getInstance().updateNumberedBlocks(updatedList);
                Log.d("numberedList", "updatedList = " + Map.getInstance().getNumberedBlocks());

                if (menu_auto_update_map != null && menu_auto_update_map.isChecked()) {
                    loadGrid();
                }

            }

            /**
             * RPI send robot status
             * formerly STATUS command
             */
            if(robot_status.length() != 0){
                switch (robot_status){
                    case "0":
                        updateStatus("Stop");
                        break;
                    case "1":
                        updateStatus("Forward");
                        break;
                    case "2":
                        updateStatus("Turn Left");
                        break;
                    case "3":
                        updateStatus("Turn Right");
                        break;
                }
            }

//            {
//                "Position": {
//                    "dir":"0",
//                    "x":"x",
//                    "y":"y"
//                },
//                "Map": {
//                    "p1":"00102012",
//                    "p2":"190"
//                }
            //  imgID[1, 2, 4, 5, 6, 7]
//             }

//            String msg = readMsg.substring(1,readMsg.length()-1); // remove curly brackets for commands
//            Log.e( "TESTE", "msg = " + msg);
//
//            String message[];
//
//            // - delimiter for imgReg, : delimiter for everything else
//            if(readMsg.contains(":")) {
//                message = msg.split(":");
//                message[0] = message[0].substring(1, message[0].length()-2).toUpperCase(); // removes inverted commas for commands
//                Log.e( "TESTE", "msg[0] = " + message[0]);
//            }else{
//                message = readMsg.split("-");
//            }
//
//            if (message[0].equals("GRID")) { //receive mapDescriptor from Algo for fastest path
//                message[1] = message[1].substring(2, message[1].length()-1);
//                Log.e( "TESTE", "msg[1] = " + message[1]);
//                Map.getInstance().setMapJson(message[1]);
//                if (menu_auto_update_map != null && menu_auto_update_map.isChecked()) {
//                    loadGrid();
//                }
//            }
//
//            else if (message[0].equals("DATA")) { //receive full data string (xCoor, yCoor, robotDirection, P1, P2) from Algo for exploration task
//                String data[] = message[1].split(",");
//
//                Map.getInstance().setMap(data[3], "", data[4]);
//
//                r.setPosX(Float.parseFloat(data[0]));
//                r.setPosY(Float.parseFloat(data[1]));
//                r.setDirection(data[2]);
//
//                if (menu_auto_update_map != null && menu_auto_update_map.isChecked()) {
//                    loadGrid();
//                }
//            }
//
//
//            //Updating of numbered blocks on obstacles on the map
//            else if (message[0].equals("BLOCK")) { //receive numbered block
//                Log.e("TESTE", "BLOCK[1] = " + message[1]);
//                String data[] = message[1].split(","); //x, y, id
//                Log.e("TESTE", "DATA[0] = " + data[0]);
//                Log.e("TESTE", "DATA[1] = " + data[1]);
//                Log.e("TESTE", "DATA[2] = " + data[2]);
//
//
//                if(Integer.parseInt(data[0]) <= 15 && Integer.parseInt(data[0]) >= 1) {
//
//                    IDblock input = new IDblock(data[0].toUpperCase(), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
//                    Log.e("TESTE", input.getID());
//                    Map.getInstance().addNumberedBlocks(input);
//                    if (menu_auto_update_map != null && menu_auto_update_map.isChecked()) {
//                        loadGrid();
//                    }
//                }
//            }
//
//            //Updating of robot's position on the map
//            else if (message[0].equals("ROBOTPOSITION")) { //receive robot position when robot moves
//                message[1] = message[1].substring(2, message[1].length()-1);
//                Log.e("TESTE", "message[1] = " + message[1]);
//                String posAndDirect[] = message[1].split(",");
//                r.setPosX(Float.parseFloat(posAndDirect[0])+1);
//                r.setPosY(Float.parseFloat(posAndDirect[1])+1);
//                r.setDirection(posAndDirect[2].substring(1));
//
////                for checklist
////                r.setPosX(Float.parseFloat(posAndDirect[0])+1);
////                r.setPosY(((Float.parseFloat(posAndDirect[1])+1) - 19) * -1);
////                Log.e("TESTE", "direction = " + posAndDirect[2]);
////                r.setDirection(posAndDirect[2].substring(1));
//
//                if (menu_auto_update_map != null && menu_auto_update_map.isChecked()) {
//                    loadGrid();
//                }
//            }
//
//            //Updating of robot's status
//            else if(message[0].equals("STATUS")){
//
////                for checklist
////                updateStatus(message[1]);
//
//                if (message[1].equals("F")) {
//                    updateStatus("Moving Forward");
//                }
//                if (message[1].equals("TR")) {
//                    updateStatus("Turning Right");
//                }
//                if (message[1].equals("TL")) {
//                    updateStatus("Turning Left");
//                }
//                if (message[1].equals("FP")) {
//                    updateStatus("Fastest Path");
//                }
//                if (message[1].equals("EX")) {
//                    updateStatus("Exploration");
//                }
//                if (message[1].equals("DONE")) {
//                    updateStatus("Done!");
//                }
//            }
//            else if(message[0].trim().equals("Y")){ //harmonize with algo
//                updateStatus("Moving");
//            }
//            else if(message[0].trim().equals("F")){ //harmonize with algo
//                updateStatus("Done!");
//            }
//            else {
//                updateStatus("Invalid Message");
//            }
        }
        Log.d("Debug", "dir = " + dir);
        Log.d("Debug", "xCoor = " + xCoor);
        Log.d("Debug", "yCoor = " + yCoor);
        Log.d("Debug", "p1 = " + p1);
        Log.d("Debug", "p2 = " + p2);
        Log.d("Debug", "robot_status = " + robot_status);
    }


    //method to send out message to rpi thru bluetooth -kept for backward compatibility
    public boolean outgoingMessage(String sendMsg, boolean isQuery) {
        //return fragment.sendMsg("@t" + sendMsg + "!");
        if(isQuery) {
            return fragment.sendMsg(sendMsg, true);
        }
        else {
            return fragment.sendMsg(sendMsg, false);
        }
    }

    public boolean outgoingMessage(String sendMsg, int destination) {
        //add delimiters
        // 0=algo/tcp, 1=ardu/serial
        if(destination == 0){
            sendMsg = "@t" + sendMsg + "!";
        }else if(destination == 1){
            sendMsg = "@s" + sendMsg + "|!";
        }
        return fragment.sendMsg(sendMsg, false);
    }

    //on the coordinate tapped
    //set waypoint, if menuitem is checked
    //set robot, if menuitem is checked
    public void onGridTapped(int posX, int posY) {
        if(menu_set_robot_position.isChecked()){
            Robot r = Robot.getInstance();
            r.setPosX(posX);
            r.setPosY(posY);
            r.setDirection("N");

            JSONObject obj;

            try {
                obj = new JSONObject().put("x", posX);
                obj = obj.put("y", posX);
                obj = obj.put("dir", "N");

                String jsonMsg = obj.toString();

                String instruction = "SetStartPoint\\" + jsonMsg + ";";

                outgoingMessage(instruction, false);

                Log.d("Debug", "Set start message = " + instruction);

//            outgoingMessage("START:"+(int)posX+","+(int)posY, 0);
                //Make a prompt here to confirm
                menu_set_robot_position.setChecked(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        if(menu_set_waypoint.isChecked()){
            Position p = new Position(posX,posY);
            WayPoint.getInstance().setPosition(p);
            outgoingMessage("SetWayPoint\\{\"x\":" + (int)posX + ",\"y\":" + (int)posY + "};", false);

//            outgoingMessage("WP:"+(int)posX+","+(int)posY, 0);
            //Make a prompt here to confirm
            menu_set_waypoint.setChecked(false);
        }
        loadGrid();
    }

    //clear waypoint
    private void removeWaypoint(){
        WayPoint.getInstance().setPosition(null);
        outgoingMessage("RemoveWayPoint\\{};", false);
        loadGrid();
    }

    //calibrate robot
    private void calibrateRobot(){
        String instruction = "Calibrate\\{};";
        outgoingMessage(instruction, false);
    }

    //swipe gesture input
    public void onSwipeTop() {

        //check if robot is out of bounds
        if(!Robot.getInstance().rotateToNorth()){
            if(!Robot.getInstance().isOutOfBounds()) {
                try {
                    String msgToBeEncoded = "1";
                    String encodedMsg = Base64.encodeToString(msgToBeEncoded.getBytes(), Base64.NO_WRAP);
                    String instructionValue = new JSONObject().put("step", encodedMsg).toString();
                    String instruction = "Forward\\" + instructionValue + ";";
                    outgoingMessage(instruction, false);
                    //getack
                    Robot.getInstance().moveForward(10);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //outgoingMessage("W", 1);
                //getack
//                Robot.getInstance().moveForward(10);
            }
        }
        else{
            Integer count = Robot.getInstance().getCount();
            sendMsgOnSwipe(count);
            //getack
            Robot.getInstance().rotateRobotToNorth();
        }
        loadGrid();
    }

    public void onSwipeLeft() {
        if(!Robot.getInstance().rotateToWest()){
            if(!Robot.getInstance().isOutOfBounds()) {
                try {
                    String msgToBeEncoded = "1";
                    String encodedMsg = Base64.encodeToString(msgToBeEncoded.getBytes(), Base64.NO_WRAP);
                    String instructionValue = new JSONObject().put("step", encodedMsg).toString();
                    String instruction = "Forward\\" + instructionValue + ";";
                    outgoingMessage(instruction, false);
                    //getack
                    Robot.getInstance().moveForward(10);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //outgoingMessage("W", 1);
                //getack
//                Robot.getInstance().moveForward(10);
            }
        }
        else{
            Integer count = Robot.getInstance().getCount();
            sendMsgOnSwipe(count);
            //getack
            Robot.getInstance().rotateRobotToWest();
        }

        loadGrid();
    }

    public void onSwipeRight() {
        if(!Robot.getInstance().rotateToEast()){
            if(!Robot.getInstance().isOutOfBounds()) {
                try {
                    String msgToBeEncoded = "1";
                    String encodedMsg = Base64.encodeToString(msgToBeEncoded.getBytes(), Base64.NO_WRAP);
                    String instructionValue = new JSONObject().put("step", encodedMsg).toString();
                    String instruction = "Forward\\" + instructionValue + ";";
                    outgoingMessage(instruction, false);
                    //getack
                    Robot.getInstance().moveForward(10);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //outgoingMessage("W", 1);
                //getack
//                Robot.getInstance().moveForward(10);
            }
        }
        else{
            Integer count = Robot.getInstance().getCount();
            sendMsgOnSwipe(count);
            //getack
            Robot.getInstance().rotateRobotToEast();
        }
        loadGrid();
    }

    public void onSwipeBottom() {
        if(!Robot.getInstance().rotateToSouth()){
            if(!Robot.getInstance().isOutOfBounds()) {
                try {
                    String msgToBeEncoded = "1";
                    String encodedMsg = Base64.encodeToString(msgToBeEncoded.getBytes(), Base64.NO_WRAP);
                    String instructionValue = new JSONObject().put("step", encodedMsg).toString();
                    String instruction = "Forward\\" + instructionValue + ";";
                    outgoingMessage(instruction, false);
                    //getack
                    Robot.getInstance().moveForward(10);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //outgoingMessage("W", 1);
                //getack
//                Robot.getInstance().moveForward(10);
            }
        }
        else{
            Integer count = Robot.getInstance().getCount();
            sendMsgOnSwipe(count);
            //getack
            Robot.getInstance().rotateRobotToSouth();
        }
        loadGrid();
    }

    public void sendMsgOnSwipe(Integer count){

        if(count==1){
            outgoingMessage("D", 1);
        }
        if(count==2){
            outgoingMessage("D", 1);
            outgoingMessage("D", 1);
        }
        if(count==-1){
            outgoingMessage("A", 1);
        }
    }

    @Override
    protected void onStop() {
        // call the superclass method first
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
