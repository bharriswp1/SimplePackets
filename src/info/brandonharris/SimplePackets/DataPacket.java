package info.brandonharris.SimplePackets;

import java.io.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by brandon on 11/22/15.
 */
public class DataPacket {
    private String privkey;
    private Map<String, String> stringData;
    private Map<String, byte[]> byteData;
    private Map<String, byte[]> unencryptedByteData;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;


    public DataPacket(Map<String, String> stringData, Map<String, byte[]> byteData, Map<String, byte[]> unencryptedByteData) {
        this.byteData = byteData;
        this.stringData = stringData;
        this.unencryptedByteData = unencryptedByteData;
    }

    public DataPacket(Map<String, String> stringData) {
        this.byteData = new HashMap<>();
        this.unencryptedByteData = new HashMap<>();
        this.stringData = stringData;
    }

    public DataPacket() {
        this.byteData = new HashMap<>();
        this.stringData = new HashMap<>();
        this.unencryptedByteData = new HashMap<>();
    }

    public void setPrivateKey(String key) {
        privkey = key;
    }

    public void sendDataEncrypted(Socket socket) throws IOException {
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(System.currentTimeMillis());
        byte[] pubkey = new byte[16];
        secureRandom.nextBytes(pubkey);
        dataOutputStream.write(pubkey);
        Iterator<String> keySet = stringData.keySet().iterator();
        while (keySet.hasNext()) {
            String key = keySet.next();
            String encryptedKey = Encryptor.encrypt(privkey, pubkey, key);
            String value = Encryptor.encrypt(privkey, pubkey, stringData.get(key));
            dataOutputStream.writeInt(encryptedKey.length());
            dataOutputStream.writeBytes(encryptedKey);
            dataOutputStream.writeInt(value.length());
            dataOutputStream.writeBytes(value);
        }
        dataOutputStream.writeInt(0xFFFFFFFF);
        keySet = byteData.keySet().iterator();
        while (keySet.hasNext()) {
            String key = keySet.next();
            String encryptedKey = Encryptor.encrypt(privkey, pubkey, key);
            byte[] value = Encryptor.encrypt(privkey, pubkey, byteData.get(key));
            dataOutputStream.writeInt(encryptedKey.length());
            dataOutputStream.writeBytes(encryptedKey);
            dataOutputStream.writeInt(value.length);
            dataOutputStream.write(value,0,value.length);
        }
        dataOutputStream.writeInt(0xFFFFFFFF);
        keySet = unencryptedByteData.keySet().iterator();
        while (keySet.hasNext()) {
            String key = keySet.next();
            byte[] value = unencryptedByteData.get(key);
            dataOutputStream.writeInt(key.length());
            dataOutputStream.writeBytes(key);
            dataOutputStream.writeInt(value.length);
            dataOutputStream.write(value,0,value.length);
        }
        dataOutputStream.writeInt(0xFFFFFFFF);
        dataOutputStream.flush();
//            dataOutputStream.close();
    }

    public void sendData(Socket socket) throws IOException {
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        Iterator<String> keySet = stringData.keySet().iterator();
        while (keySet.hasNext()) {
            String key = keySet.next();
            String value = stringData.get(key);
            dataOutputStream.writeInt(key.length());
            dataOutputStream.writeBytes(key);
            dataOutputStream.writeInt(value.length());
            dataOutputStream.writeBytes(value);
        }
        dataOutputStream.writeInt(0xFFFFFFFF);
        keySet = byteData.keySet().iterator();
        while (keySet.hasNext()) {
            String key = keySet.next();
            byte[] value = byteData.get(key);
            dataOutputStream.writeInt(key.length());
            dataOutputStream.writeBytes(key);
            dataOutputStream.writeInt(value.length);
            dataOutputStream.write(value,0,value.length);
        }
        dataOutputStream.writeInt(0xFFFFFFFF);
        keySet = unencryptedByteData.keySet().iterator();
        while (keySet.hasNext()) {
            String key = keySet.next();
            byte[] value = unencryptedByteData.get(key);
            dataOutputStream.writeInt(key.length());
            dataOutputStream.writeBytes(key);
            dataOutputStream.writeInt(value.length);
            dataOutputStream.write(value,0,value.length);
        }
        dataOutputStream.writeInt(0xFFFFFFFF);
        dataOutputStream.flush();
//            dataOutputStream.close();
    }

    public void getDataEncrypted(Socket socket) throws IOException {
        dataInputStream = new DataInputStream(socket.getInputStream());
        byte[] pubkey = new byte[16];
        dataInputStream.readFully(pubkey, 0, 16);
        while (true) {
            int length = dataInputStream.readInt();
            if (length == 0xFFFFFFFF) {
                break;
            }

            byte[] bytes = new byte[length];
            dataInputStream.readFully(bytes,0,length);
            String key = Encryptor.decrypt(privkey, pubkey, new String(bytes));

            length = dataInputStream.readInt();
            if (length == 0xFFFFFFFF) {
                break;
            }

            bytes = new byte[length];
            dataInputStream.readFully(bytes, 0, length);
            String value = Encryptor.decrypt(privkey, pubkey, new String(bytes));
            stringData.put(key, value);
        }
        while (true) {
            int length = dataInputStream.readInt();
            if (length == 0xFFFFFFFF) {
                break;
            }

            byte[] bytes = new byte[length];
            dataInputStream.readFully(bytes,0,length);
            String key = Encryptor.decrypt(privkey, pubkey, new String(bytes));

            length = dataInputStream.readInt();
            if (length == 0xFFFFFFFF) {
                break;
            }

            bytes = new byte[length];
            dataInputStream.readFully(bytes,0,length);
            byte[] value = Encryptor.decrypt(privkey, pubkey, bytes);
            byteData.put(key,value);
        }
        while (true) {
            int length = dataInputStream.readInt();
            if (length == 0xFFFFFFFF) {
                break;
            }

            byte[] bytes = new byte[length];
            dataInputStream.readFully(bytes,0,length);
            String key = new String(bytes);

            length = dataInputStream.readInt();
            if (length == 0xFFFFFFFF) {
                break;
            }

            bytes = new byte[length];
            dataInputStream.readFully(bytes,0,length);
            unencryptedByteData.put(key,bytes);
        }
    }

    public void getData(Socket socket) throws IOException {
        dataInputStream = new DataInputStream(socket.getInputStream());
        while (true) {
            int length = dataInputStream.readInt();
            if (length == 0xFFFFFFFF) {
                break;
            }

            byte[] bytes = new byte[length];
            dataInputStream.readFully(bytes,0,length);
            String key = new String(bytes);

            length = dataInputStream.readInt();
            if (length == 0xFFFFFFFF) {
                break;
            }

            bytes = new byte[length];
            dataInputStream.readFully(bytes, 0, length);
            String value = new String(bytes);
            stringData.put(key, value);
        }
        while (true) {
            int length = dataInputStream.readInt();
            if (length == 0xFFFFFFFF) {
                break;
            }

            byte[] bytes = new byte[length];
            dataInputStream.readFully(bytes,0,length);
            String key = new String(bytes);

            length = dataInputStream.readInt();
            if (length == 0xFFFFFFFF) {
                break;
            }

            bytes = new byte[length];
            dataInputStream.readFully(bytes,0,length);
            byte[] value = bytes;
            byteData.put(key,value);
        }
        while (true) {
            int length = dataInputStream.readInt();
            if (length == 0xFFFFFFFF) {
                break;
            }

            byte[] bytes = new byte[length];
            dataInputStream.readFully(bytes,0,length);
            String key = new String(bytes);

            length = dataInputStream.readInt();
            if (length == 0xFFFFFFFF) {
                break;
            }

            bytes = new byte[length];
            dataInputStream.readFully(bytes,0,length);
            unencryptedByteData.put(key,bytes);
        }
    }

    public String getString(String key) {
        return stringData.get(key);
    }

    public byte[] getBytes(String key) {
        return byteData.get(key);
    }

    public byte[] getUnencryptedBytes(String key) {
        return unencryptedByteData.get(key);
    }

    public void setString(String key, String value) {
        stringData.put(key, value);
    }

    public void setBytes(String key, byte[] value) {
        byteData.put(key, value);
    }

    public void setUnencryptedBytes(String key, byte[] value) {
        unencryptedByteData.put(key, value);
    }

    public void setSerializable(String key, Object value) {
        setBytes(key, objectToBytes(value));
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{\n    ");
        Iterator<String> iterator = stringData.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = stringData.get(key);
            stringBuilder.append(key + ": " + value + "\n    ");
        }
        stringBuilder.append("\n}");
        return stringBuilder.toString();
    }

    public Object getSerializable(String key) {
        return bytesToObject(byteData.get(key));
    }

    public static byte[] objectToBytes(Object object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] bytes = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            bytes = bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return bytes;
    }

    public static Object bytesToObject(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        Object object = null;
        try {
            in = new ObjectInputStream(bis);
            object = in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return object;
    }
}
