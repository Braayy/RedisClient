package braayy.redis;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RedisClient {

    private static final String SET_COMMAND = "SET \"%s\" \"%s\"\n";
    private static final String GET_COMMAND = "GET \"%s\"\n";
    private static final String LPUSH_COMMAND = "LPUSH \"%s\" %s\n";
    private static final String LRANGE_COMMAND = "LRANGE \"%s\" %d %d\n";
    private static final String LPOP_COMMAND = "LPOP \"%s\"\n";
    private static final String LLEN_COMMAND = "LLEN \"%s\"\n";

    private Socket socket;
    private String host;
    private int port;

    public RedisClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        this.socket = new Socket(this.host, this.port);
    }

    public void set(String key, Object value) throws IOException {
        OutputStream out = this.socket.getOutputStream();

        out.write(String.format(SET_COMMAND, key, value.toString()).getBytes(StandardCharsets.UTF_8));
        out.flush();

        InputStream in = this.socket.getInputStream();

        while (true) {
            int data = in.read();

            if (data < 0 || data == 10) break;
        }
    }

    public String get(String key) throws IOException {
        OutputStream out = this.socket.getOutputStream();
        out.write(String.format(GET_COMMAND, key).getBytes(StandardCharsets.UTF_8));
        out.flush();

        StringBuilder builder = new StringBuilder();

        InputStream in = this.socket.getInputStream();
        byte[] buffer = new byte[1024];

        while (true) {
            int c = in.read(buffer, 0, buffer.length);

            String data = new String(buffer, 0, c);

            builder.append(data);

            if (data.charAt(c - 1) == 10) break;
        }

        String data = builder.toString();
        String[] splitData = data.split("\r\n");

        return splitData[1];
    }

    public int lpush(String key, Object... values) throws IOException {
        StringBuilder builder = new StringBuilder();

        for (Object value : values) {
            builder.append("\"").append(value.toString()).append("\" ");
        }

        builder.deleteCharAt(builder.length() - 1);

        OutputStream out = this.socket.getOutputStream();
        out.write(String.format(LPUSH_COMMAND, key, builder).getBytes(StandardCharsets.UTF_8));
        out.flush();

        InputStream in = this.socket.getInputStream();
        byte[] buffer = new byte[23];

        int c = in.read(buffer);

        if (c > 0) {
            String data = new String(buffer, 0, c);

            String size = data.substring(1, data.length() - 2);

            return Integer.parseInt(size);
        }

        throw new IllegalStateException("socket returned no data");
    }

    public List<String> lrange(String key, int start, int end) throws IOException {
        OutputStream out = this.socket.getOutputStream();
        out.write(String.format(LRANGE_COMMAND, key, start, end).getBytes(StandardCharsets.UTF_8));
        out.flush();

        StringBuilder builder = new StringBuilder();

        InputStream in = this.socket.getInputStream();

        byte[] buffer = new byte[1024];
        while (true) {
            int c = in.read(buffer);

            String data = new String(buffer, 0, c);

            builder.append(data);

            if (data.charAt(data.length() - 1) == 10) break;
        }

        List<String> list = new ArrayList<>();

        String[] data = builder.toString().split("\r\n");

        data = Arrays.copyOfRange(data, 1, data.length);

        for (int i = 1; i < data.length; i += 2) {
            list.add(data[i]);
        }

        return list;
    }

    public String lpop(String key) throws IOException {
        OutputStream out = this.socket.getOutputStream();
        out.write(String.format(LPOP_COMMAND, key).getBytes(StandardCharsets.UTF_8));
        out.flush();

        StringBuilder builder = new StringBuilder();

        InputStream in = this.socket.getInputStream();
        byte[] buffer = new byte[1024];

        while (true) {
            int c = in.read(buffer, 0, buffer.length);

            String data = new String(buffer, 0, c);

            builder.append(data);

            if (data.charAt(c - 1) == 10) break;
        }

        String data = builder.toString();
        String[] splitData = data.split("\r\n");

        return splitData[1];
    }

    public int llen(String key) throws IOException {
        OutputStream out = this.socket.getOutputStream();
        out.write(String.format(LLEN_COMMAND, key).getBytes(StandardCharsets.UTF_8));
        out.flush();

        InputStream in = this.socket.getInputStream();
        byte[] buffer = new byte[23];

        int c = in.read(buffer);

        if (c > 0) {
            String data = new String(buffer, 0, c);

            String size = data.substring(1, data.length() - 2);

            return Integer.parseInt(size);
        }

        throw new IllegalStateException("socket returned no data");
    }

    public void close() throws IOException {
        this.socket.close();
    }

    private static void printInput(InputStream in) throws IOException {
        while (true) {
            int data = in.read();

            System.out.println(data + "->" + (char) data);
        }
    }

}
