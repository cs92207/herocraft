package de.christoph.herocraft.challenges;

import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class ChallengeManager implements Listener {

    public ArrayList<Challenge> challenges;

    public ChallengeManager() {
        challenges = new ArrayList<>();
        loadChallenges();
    }

    private void loadChallenges() {
        challenges.addAll(List.of(new Challenge[]{
            new Challenge("§4§lSchneemann", "§7Baut einen Schneemann in eurer Base und fotografiert ihn. Schickt das Foto in den #HeroCraft Channel"),
            new Challenge("§4§lKuchenpause", "§7Baut einen Schön dekorierten Tisch mit einem Kuchen und fotografiert ihn. Schickt das Foto in den #HeroCraft Channel"),
            new Challenge("§4§lSchneeballschlacht", "§7Sammelt ein Paar schneebälle Startet eine Schneeballschlacht untereinander und fotografiert sie. Schickt das Foto in den #HeroCraft Channel"),
            new Challenge("§4§lSelfie Time", "§7Mach ein Schönes Selfie mit einem Ingame Freund vor dem Sonnenuntergang Postet dies im Chat mit #HeroCraft"),
            new Challenge("§4§lWeihnachtsschmücken", "§7Schmücke dein Haus / Deine Base weihnachtlich und Macht im Stream / Video eine Schöne Roomtour"),
            new Challenge("§4§l5 Sterne Essen?", "§7Denke dir ein Schönes Essen Menü aus und Lege dies in Eine Kiste"),
        }));
    }

    public ArrayList<Challenge> getChallenges() {
        return challenges;
    }

}
