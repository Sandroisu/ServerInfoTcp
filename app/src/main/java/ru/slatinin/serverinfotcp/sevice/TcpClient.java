package ru.slatinin.serverinfotcp.sevice;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import ru.slatinin.serverinfotcp.pack.MetaPackage;
import ru.slatinin.serverinfotcp.pack.PackageReadUtils;
import ru.slatinin.serverinfotcp.pack.PackageUtil;
import ru.slatinin.serverinfotcp.pack.RPCResult;

public class TcpClient {
    public String server_address; // your computer IP
    // address
    public String server_port;
    // sends message received notifications
    private OnMessageReceivedListener mMessageListener = null;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;

    private String uid;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages
     * received from server
     */
    public TcpClient(String address, String port, OnMessageReceivedListener listener) {
        mMessageListener = listener;
        server_port = port;
        server_address = address;
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(String message) {
        if (mBufferOut != null && !mBufferOut.checkError()) {
            mBufferOut.print(message);
            mBufferOut.flush();
        }
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {
        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
    }

    public void run() {

        mRun = true;

        try {
            InetAddress serverAddr = InetAddress.getByName(server_address);

            int port = Integer.parseInt(server_port);
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(serverAddr, port), 10000);
            try {

                mBufferOut = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
                sendMessage("Hello, World!!!");
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (mRun) {
                    String message = mBufferIn.readLine();
                    if (message != null && message.length() > 16) {
                        byte[] y = message.getBytes();
                        MetaPackage metaPackage = PackageUtil.readMeta(y, false);
                        PackageReadUtils packageReadUtils = new PackageReadUtils(y, false);
                        RPCResult[] results = packageReadUtils.getFromResult();
                        if (mMessageListener != null && results.length > 0 && metaPackage != null) {
                            if (results[0].result != null) {
                                mMessageListener.onServerMessageReceived(results[0].result.records, metaPackage.id, metaPackage.dataInfo);
                            }
                        }
                    } else {
                        mMessageListener.onErrorOccurred(message);
                        sendMessage("Hello, World!!!");
                    }
                }
            } catch (Exception e) {
                if (mMessageListener != null) {
                    mMessageListener.onErrorOccurred(getStackTrace(e));
                }
            } finally {
                socket.close();
            }

        } catch (Exception e) {
            if (mMessageListener != null) {
                mMessageListener.onErrorOccurred(getStackTrace(e));
            }

        }

    }

    private String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public interface OnMessageReceivedListener {
        void onServerMessageReceived(JsonObject[] object, String ip, String dataInfo);

        void onErrorOccurred(String message);
    }

}
