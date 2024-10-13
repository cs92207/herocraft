package de.christoph.herocraft.armee;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.christoph.herocraft.HeroCraft;
import de.christoph.herocraft.lands.Land;
import de.christoph.herocraft.utils.Constant;
import jline.internal.Nullable;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SuperheroManager implements Listener {

    private ArrayList<Superhero> allHeroes;
    private ArrayList<Superhero> normalHeroes;
    private ArrayList<Superhero> rareHeroes;
    private ArrayList<Superhero> epicHeroes;
    private ArrayList<Superhero> legendHeroes;

    public SuperheroManager() {
        loadHeroes();
    }

    private void loadHeroes() {
        allHeroes = new ArrayList<>();
        normalHeroes = new ArrayList<>();
        rareHeroes = new ArrayList<>();
        epicHeroes = new ArrayList<>();
        legendHeroes = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(new File(Constant.HEROES_PATH));
            ArrayNode allHeroesNode = (ArrayNode) jsonNode;
            Iterator<JsonNode> iterator = allHeroesNode.elements();
            while (iterator.hasNext()) {
                JsonNode heroNode = iterator.next();
                int id = heroNode.get("id").asInt();
                String name = heroNode.get("name").asText();
                EntityType entityType = EntityType.valueOf(heroNode.get("entityType").asText());
                ArrayList<PotionEffectType> skills = new ArrayList<>();
                ArrayNode skillsNode = (ArrayNode) heroNode.get("skills");
                for (JsonNode skill : skillsNode) {
                    skills.add(PotionEffectType.getByName(skill.asText()));
                }
                ArrayList<Integer> amplifiers = new ArrayList<>();
                ArrayNode amplifiersNode = (ArrayNode) heroNode.get("amplifiers");
                for (JsonNode amplifier : amplifiersNode) {
                    amplifiers.add(amplifier.asInt());
                }
                Rarity rarity = Rarity.valueOf(heroNode.get("rarity").asText());
                Superhero superhero = new Superhero(id, name, entityType, skills, amplifiers, rarity);
                allHeroes.add(superhero);
                switch (superhero.getRarity()) {
                    case NORMAL:
                        normalHeroes.add(superhero);
                        break;
                    case RARE:
                        rareHeroes.add(superhero);
                        break;
                    case EPIC:
                        epicHeroes.add(superhero);
                    case LEGEND:
                        legendHeroes.add(superhero);
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Superhero> getUnlockedHeroes(Land land) {
        ArrayList<Superhero> superheroes = new ArrayList<>();
        String heroesString = "";
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT `unlocked_heroes` FROM `armee` WHERE `land` = ?");
            preparedStatement.setString(1, land.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                heroesString = resultSet.getString("unlocked_heroes");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for(String i : heroesString.split(",")) {
            int id = Integer.parseInt(i);
            superheroes.add(getHeroByID(id));
        }
        return superheroes;
    }

    @Nullable
    public Superhero getHeroByID(int id) {
        for(Superhero superhero : HeroCraft.getPlugin().getSuperheroManager().getAllHeroes()) {
            if(superhero.getId() == id) {
                return superhero;
            }
        }
        return null;
    }

    public double getResearchCosts(Land land) {
        double researchCosts = 0;
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT `research_costs` FROM `armee` WHERE `land` = ?");
            preparedStatement.setString(1, land.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                researchCosts = resultSet.getDouble("research_costs");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return researchCosts;
    }

    public ArrayList<Superhero> getCreatedHeroes(Land land) {
        ArrayList<Superhero> superheroes = new ArrayList<>();
        String heroesString = "";
        try {
            PreparedStatement preparedStatement = HeroCraft.getPlugin().getMySQL().getConnection().prepareStatement("SELECT `created_heroes` FROM `armee` WHERE `land` = ?");
            preparedStatement.setString(1, land.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                heroesString = resultSet.getString("created_heroes");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for(String i : heroesString.split(",")) {
            int id = Integer.parseInt(i);
            superheroes.add(getHeroByID(id));
        }
        return superheroes;
    }

    public ArrayList<Superhero> getAllHeroes() {
        return allHeroes;
    }

}