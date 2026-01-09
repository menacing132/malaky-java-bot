import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

public class Main {

    // 🔐 التوكن من Secrets (Replit)
    static final String TOKEN = System.getenv("BOT_TOKEN");

    // 📢 روم الديسكورد
    static final String CHANNEL_ID = "PUT_CHANNEL_ID_HERE";

    // 🟦 معلومات السيرفر
    static final String SERVER_NAME = "MALAKY SERVER";
    static final String JAVA_IP = "malaky.xyz";
    static final String BEDROCK_IP = "malaky.xyz";
    static final String BEDROCK_PORT = "19132";

    static TextChannel channel;

    public static void main(String[] args) throws Exception {

        JDA jda = JDABuilder.createDefault(TOKEN).build();
        jda.awaitReady();

        channel = jda.getTextChannelById(CHANNEL_ID);

        // تحديث كل 5 دقائق
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendStatus();
            }
        }, 0, 5 * 60 * 1000);
    }

    static void sendStatus() {
        try {
            ServerStatus status = getStatus();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("~ " + SERVER_NAME);
            embed.setColor(status.online ? Color.GREEN : Color.RED);

            embed.addField("🧩 Java Edition IP", "`" + JAVA_IP + "`", false);
            embed.addField(
                    "🪨 Bedrock Edition IP",
                    "`" + BEDROCK_IP + "`\nPort: **" + BEDROCK_PORT + "**",
                    false
            );

            embed.addField(
                    "🔴 Status | الحالة",
                    status.online ? "🟢 Online" : "🔴 Offline",
                    false
            );

            embed.addField(
                    "🌐 Players | اللاعبين",
                    status.players,
                    false
            );

            embed.setFooter("Malaky Network");

            channel.sendMessageEmbeds(embed.build()).queue();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static ServerStatus getStatus() {
        try {
            URL url = new URL("https://api.mcsrvstat.us/2/" + JAVA_IP);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner sc = new Scanner(conn.getInputStream());
            StringBuilder json = new StringBuilder();
            while (sc.hasNext()) json.append(sc.nextLine());

            JSONObject obj = new JSONObject(json.toString());

            if (!obj.getBoolean("online")) {
                return new ServerStatus(false, "0 / 0");
            }

            JSONObject players = obj.getJSONObject("players");
            return new ServerStatus(
                    true,
                    players.getInt("online") + " / " + players.getInt("max")
            );

        } catch (Exception e) {
            return new ServerStatus(false, "0 / 0");
        }
    }

    static class ServerStatus {
        boolean online;
        String players;

        ServerStatus(boolean online, String players) {
            this.online = online;
            this.players = players;
        }
    }
}

