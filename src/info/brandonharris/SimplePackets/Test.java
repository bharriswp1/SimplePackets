package info.brandonharris.SimplePackets;

import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by brandon on 6/24/16.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        stringTest(22222);

        //Private key must be 16 characters
        stringTestEncrypted(22223, "1234567890123456");

        bytesTest(22224);

        bytesTestEncrypted(22225, "1234567890123456");

        unencryptedBytesTest(22226);

        unencryptedBytesTestEncrypted(22227, "1234567890123456");

        testObject(22228);

        testObjectEncrypted(22229, "1234567890123456");
    }

    public static void stringTest(int port) throws Exception {
        SimpleServer simpleServer = new SimpleServer(port, new ClientCallback() {
            @Override
            public void onClientConnected(Socket socket) {
                try {
                    DataPacket dataPacket = new DataPacket();
                    dataPacket.getData(socket);
                    if (dataPacket.getString("command").equals("test")) {
                        DataPacket responsePacket = new DataPacket();
                        responsePacket.setString("response", "OK");
                        responsePacket.sendData(socket);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        simpleServer.start();

        Thread.sleep(100);

        Socket socket = new Socket("localhost", port);
        DataPacket dataPacket = new DataPacket();
        dataPacket.setString("command", "test");
        dataPacket.sendData(socket);
        DataPacket responsePacket = new DataPacket();
        responsePacket.getData(socket);
        if (responsePacket.getString("response").equals("OK")) {
            System.out.println("String test: passed");
        } else {
            System.out.println("String test: failed");
        }
        simpleServer.close();
    }

    public static void stringTestEncrypted(int port, String privateKey) throws Exception {
        SimpleServer simpleServer = new SimpleServer(port, new ClientCallback() {
            @Override
            public void onClientConnected(Socket socket) {
                try {
                    DataPacket dataPacket = new DataPacket();
                    dataPacket.setPrivateKey(privateKey);
                    dataPacket.getDataEncrypted(socket);
                    if (dataPacket.getString("command").equals("test")) {
                        DataPacket responsePacket = new DataPacket();
                        responsePacket.setPrivateKey(privateKey);
                        responsePacket.setString("response", "OK");
                        responsePacket.sendDataEncrypted(socket);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        simpleServer.start();

        Thread.sleep(100);

        Socket socket = new Socket("localhost", port);
        DataPacket dataPacket = new DataPacket();
        dataPacket.setPrivateKey(privateKey);
        dataPacket.setString("command", "test");
        dataPacket.sendDataEncrypted(socket);
        DataPacket responsePacket = new DataPacket();
        responsePacket.setPrivateKey(privateKey);
        responsePacket.getDataEncrypted(socket);
        if (responsePacket.getString("response").equals("OK")) {
            System.out.println("Encrypted String test: passed");
        } else {
            System.out.println("Encrypted String test: failed");
        }
        simpleServer.close();
    }

    public static void bytesTest(int port) throws Exception {
        Random random = new Random(System.currentTimeMillis());
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);

        SimpleServer simpleServer = new SimpleServer(port, new ClientCallback() {
            @Override
            public void onClientConnected(Socket socket) {
                try {
                    DataPacket dataPacket = new DataPacket();
                    dataPacket.getData(socket);
                    byte[] testBytes = dataPacket.getBytes("testBytes");
                    if (!Arrays.equals(testBytes, bytes)) {
                        System.out.println("Bytes test failed on server end");
                    }
                    DataPacket responsePacket = new DataPacket();
                    responsePacket.setBytes("testBytes", testBytes);
                    responsePacket.sendData(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        simpleServer.start();

        Thread.sleep(100);

        Socket socket = new Socket("localhost", port);
        DataPacket dataPacket = new DataPacket();
        dataPacket.setBytes("testBytes", bytes);
        dataPacket.sendData(socket);
        DataPacket responsePacket = new DataPacket();
        responsePacket.getData(socket);
        if (Arrays.equals(responsePacket.getBytes("testBytes"), bytes)) {
            System.out.println("Bytes test: passed");
        } else {
            System.out.println("Bytes test: failed");
        }
        simpleServer.close();
    }

    public static void bytesTestEncrypted(int port, String privateKey) throws Exception {
        Random random = new Random(System.currentTimeMillis());
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);

        SimpleServer simpleServer = new SimpleServer(port, new ClientCallback() {
            @Override
            public void onClientConnected(Socket socket) {
                try {
                    DataPacket dataPacket = new DataPacket();
                    dataPacket.setPrivateKey(privateKey);
                    dataPacket.getDataEncrypted(socket);
                    byte[] testBytes = dataPacket.getBytes("testBytes");
                    if (!Arrays.equals(testBytes, bytes)) {
                        System.out.println("Encrypted Bytes test failed on server end");
                    }
                    DataPacket responsePacket = new DataPacket();
                    responsePacket.setPrivateKey(privateKey);
                    responsePacket.setBytes("testBytes", testBytes);
                    responsePacket.sendDataEncrypted(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        simpleServer.start();

        Thread.sleep(100);

        Socket socket = new Socket("localhost", port);
        DataPacket dataPacket = new DataPacket();
        dataPacket.setPrivateKey(privateKey);
        dataPacket.setBytes("testBytes", bytes);
        dataPacket.sendDataEncrypted(socket);
        DataPacket responsePacket = new DataPacket();
        responsePacket.setPrivateKey(privateKey);
        responsePacket.getDataEncrypted(socket);
        if (Arrays.equals(responsePacket.getBytes("testBytes"), bytes)) {
            System.out.println("Encrypted Bytes test: passed");
        } else {
            System.out.println("Encrypted Bytes test: failed");
        }
        simpleServer.close();
    }

    public static void unencryptedBytesTest(int port) throws Exception {
        Random random = new Random(System.currentTimeMillis());
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);

        SimpleServer simpleServer = new SimpleServer(port, new ClientCallback() {
            @Override
            public void onClientConnected(Socket socket) {
                try {
                    DataPacket dataPacket = new DataPacket();
                    dataPacket.getData(socket);
                    byte[] testBytes = dataPacket.getUnencryptedBytes("testBytes");
                    if (!Arrays.equals(testBytes, bytes)) {
                        System.out.println("Unencrypted Bytes test failed on server end");
                    }
                    DataPacket responsePacket = new DataPacket();
                    responsePacket.setUnencryptedBytes("testBytes", testBytes);
                    responsePacket.sendData(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        simpleServer.start();

        Thread.sleep(100);

        Socket socket = new Socket("localhost", port);
        DataPacket dataPacket = new DataPacket();
        dataPacket.setUnencryptedBytes("testBytes", bytes);
        dataPacket.sendData(socket);
        DataPacket responsePacket = new DataPacket();
        responsePacket.getData(socket);
        if (Arrays.equals(responsePacket.getUnencryptedBytes("testBytes"), bytes)) {
            System.out.println("Unencrypted Bytes test: passed");
        } else {
            System.out.println("Unencrypted Bytes test: failed");
        }
        simpleServer.close();
    }

    public static void unencryptedBytesTestEncrypted(int port, String privateKey) throws Exception {
        Random random = new Random(System.currentTimeMillis());
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);

        SimpleServer simpleServer = new SimpleServer(port, new ClientCallback() {
            @Override
            public void onClientConnected(Socket socket) {
                try {
                    DataPacket dataPacket = new DataPacket();
                    dataPacket.setPrivateKey(privateKey);
                    dataPacket.getDataEncrypted(socket);
                    byte[] testBytes = dataPacket.getUnencryptedBytes("testBytes");
                    if (!Arrays.equals(testBytes, bytes)) {
                        System.out.println("Encrypted Unencrypted Bytes test failed on server end");
                    }
                    DataPacket responsePacket = new DataPacket();
                    responsePacket.setPrivateKey(privateKey);
                    responsePacket.setUnencryptedBytes("testBytes", testBytes);
                    responsePacket.sendDataEncrypted(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        simpleServer.start();

        Thread.sleep(100);

        Socket socket = new Socket("localhost", port);
        DataPacket dataPacket = new DataPacket();
        dataPacket.setPrivateKey(privateKey);
        dataPacket.setUnencryptedBytes("testBytes", bytes);
        dataPacket.sendDataEncrypted(socket);
        DataPacket responsePacket = new DataPacket();
        responsePacket.setPrivateKey(privateKey);
        responsePacket.getDataEncrypted(socket);
        if (Arrays.equals(responsePacket.getUnencryptedBytes("testBytes"), bytes)) {
            System.out.println("Encrypted Unencrypted Bytes test: passed");
        } else {
            System.out.println("Encrypted Unencrypted Bytes test: failed");
        }
        simpleServer.close();
    }

    public static void testObject(int port) throws Exception {
        SimpleServer simpleServer = new SimpleServer(port, new ClientCallback() {
            @Override
            public void onClientConnected(Socket socket) {
                try {
                    DataPacket dataPacket = new DataPacket();
                    dataPacket.getData(socket);
                    Integer testObject = (Integer) dataPacket.getSerializable("testObject");
                    if (testObject != 27) {
                        System.out.println("Object test failed on server end");
                    }
                    DataPacket responsePacket = new DataPacket();
                    responsePacket.setSerializable("testObject", testObject);
                    responsePacket.sendData(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        simpleServer.start();

        Thread.sleep(100);

        Socket socket = new Socket("localhost", port);
        DataPacket dataPacket = new DataPacket();
        dataPacket.setString("command", "test");
        dataPacket.setSerializable("testObject", 27);
        dataPacket.sendData(socket);
        DataPacket responsePacket = new DataPacket();
        responsePacket.getData(socket);
        if ((Integer) responsePacket.getSerializable("testObject") == 27) {
            System.out.println("Object test: passed");
        } else {
            System.out.println("Object test: failed");
        }
        simpleServer.close();
    }

    public static void testObjectEncrypted(int port, String privateKey) throws Exception {
        SimpleServer simpleServer = new SimpleServer(port, new ClientCallback() {
            @Override
            public void onClientConnected(Socket socket) {
                try {
                    DataPacket dataPacket = new DataPacket();
                    dataPacket.setPrivateKey(privateKey);
                    dataPacket.getDataEncrypted(socket);
                    Integer testObject = (Integer) dataPacket.getSerializable("testObject");
                    if (testObject != 27) {
                        System.out.println("Encrypted Object test failed on server end");
                    }
                    DataPacket responsePacket = new DataPacket();
                    responsePacket.setPrivateKey(privateKey);
                    responsePacket.setSerializable("testObject", testObject);
                    responsePacket.sendDataEncrypted(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        simpleServer.start();

        Thread.sleep(100);

        Socket socket = new Socket("localhost", port);
        DataPacket dataPacket = new DataPacket();
        dataPacket.setPrivateKey(privateKey);
        dataPacket.setString("command", "test");
        dataPacket.setSerializable("testObject", 27);
        dataPacket.sendDataEncrypted(socket);
        DataPacket responsePacket = new DataPacket();
        responsePacket.setPrivateKey(privateKey);
        responsePacket.getDataEncrypted(socket);
        if ((Integer) responsePacket.getSerializable("testObject") == 27) {
            System.out.println("Encrypted Object test: passed");
        } else {
            System.out.println("Encrypted Object test: failed");
        }
        simpleServer.close();
    }
}
