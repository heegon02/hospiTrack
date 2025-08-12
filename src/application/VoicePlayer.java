package application;

public class VoicePlayer {
    public static void speak(String text) {
        String command = String.format(
            "PowerShell -Command \"Add-Type -AssemblyName System.Speech; " +
            "(New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('%s');\"",
            text.replace("\"", "\\\"")  // 작은 따옴표 이스케이프
        );

        try {
            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

