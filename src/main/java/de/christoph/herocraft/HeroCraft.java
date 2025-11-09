package de.christoph.herocraft;

import de.christoph.herocraft.afksystem.AdminAFK;
import de.christoph.herocraft.allthemobs.AllTheMobsManager;
import de.christoph.herocraft.allthemobs.GetPointsFromLand;
import de.christoph.herocraft.allthemobs.LandMobsCommand;
import de.christoph.herocraft.allthemobs.MobListCommand;
import de.christoph.herocraft.armee.ResearchResultCommand;
import de.christoph.herocraft.armee.SuperheroManager;
import de.christoph.herocraft.basiccommands.*;
import de.christoph.herocraft.birthday.BirthdayCommand;
import de.christoph.herocraft.booster.BoosterManager;
import de.christoph.herocraft.caseopening.CaseOpeningListener;
import de.christoph.herocraft.caseopening.SetCaseOpeningCommand;
import de.christoph.herocraft.challenges.Commands;
import de.christoph.herocraft.challenges.config.Aufgabe;
import de.christoph.herocraft.challenges.config.CreateConfigs;
import de.christoph.herocraft.challenges.gui.CreateGui;
import de.christoph.herocraft.challenges.gui.GuiListener;
import de.christoph.herocraft.dimensions.DimensionCommand;
import de.christoph.herocraft.dimensions.DimensionManager;
import de.christoph.herocraft.dimensions.DimensionStorage;
import de.christoph.herocraft.dungeons.DungeonManager;
import de.christoph.herocraft.economy.Coin;
import de.christoph.herocraft.economy.CoinCommand;
import de.christoph.herocraft.home.HomeCommand;
import de.christoph.herocraft.home.HomeManager;
import de.christoph.herocraft.insurance.InsuranceGui;
import de.christoph.herocraft.insurance.InsuranceManager;
import de.christoph.herocraft.landpresentation.LandPresentationManager;
import de.christoph.herocraft.landpresentation.SetPresentationHoloLocationCommand;
import de.christoph.herocraft.landpresentation.VoteForLandCommand;
import de.christoph.herocraft.lands.*;
import de.christoph.herocraft.lands.armee.ArmeeManager;
import de.christoph.herocraft.lands.province.CityBlock;
import de.christoph.herocraft.lands.province.ProvinceManager;
import de.christoph.herocraft.lands.province.TownHall;
import de.christoph.herocraft.lands.roles.LandRoleManager;
import de.christoph.herocraft.market.*;
import de.christoph.herocraft.market.herokea.HeroKeaListener;
import de.christoph.herocraft.markethall.*;
import de.christoph.herocraft.mysql.MySQL;
import de.christoph.herocraft.prison.PrisonCommand;
import de.christoph.herocraft.prison.PrisonManager;
import de.christoph.herocraft.prison.SetPrisonSpawnPointCommand;
import de.christoph.herocraft.protection.ProtectionListener;
import de.christoph.herocraft.protection.SneakChallenge;
import de.christoph.herocraft.quests.DailyQuest;
import de.christoph.herocraft.raids.*;
import de.christoph.herocraft.school.MentorCommand;
import de.christoph.herocraft.school.MentorListener;
import de.christoph.herocraft.school.skills.SkillManager;
import de.christoph.herocraft.specialitems.*;
import de.christoph.herocraft.teleporter.Teleporter;
import de.christoph.herocraft.tutorial.*;
import de.christoph.herocraft.voteday.StartVoteDayCommand;
import de.christoph.herocraft.voteday.VoteDayManager;
import de.christoph.herocraft.voteday.VoteDayNoCommand;
import de.christoph.herocraft.voteday.VoteDayYesCommand;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class HeroCraft extends JavaPlugin {

    private static HeroCraft plugin;
    public Coin coin;
    private MySQL mySQL;
    private MySQL shopMySQL;
    private Auctioneers auctioneers;
    private LandManager landManager;
    private SuperheroManager superheroManager;
    private InsuranceManager insuranceManager;
    private VoteDayManager voteDayManager;
    private SkillManager skillManager;
    private HomeManager homeManager;
    private DungeonManager dungeonManager;
    private LandPresentationManager landPresentationManager;
    private ProvinceManager provinceManager;
    private BoosterManager boosterManager;
    private DimensionManager dimensionManager;

    public static Commands openGUICommand;
    public static CreateGui createGUI;
    public static List<Aufgabe> dailyAufgaben = new ArrayList<>();
    public static GuiListener guiListener;
    public static CreateConfigs createConfig;
    public static List<Aufgabe> allTimeAufgaben = new ArrayList<>();
    private Map<UUID, UUID> replyMap = new HashMap<>();
    public LandTagManager landTagManager;
    public LandRoleManager landRoleManager;
    public ArmeeManager armeeManager;

    public RaidManager raidManager;
    public DailyQuest dailyQuest;

    public PrisonManager prisonManager;

    @Override
    public void onEnable() {
        plugin = this;
        mySQL = new MySQL("144.76.83.19", 3306, "herocraft", "SV-Studios", "1Emanuel0602#");
        shopMySQL = new MySQL("144.76.83.19", 3306, "shop", "SV-Studios", "1Emanuel0602#");
        coin = new Coin();
        auctioneers = new Auctioneers();
        landManager = new LandManager();
        voteDayManager = new VoteDayManager();
        skillManager = new SkillManager();
        homeManager = new HomeManager();
        dungeonManager = new DungeonManager();
        provinceManager = new ProvinceManager();
        boosterManager = new BoosterManager();
        landTagManager = new LandTagManager();
        landRoleManager = new LandRoleManager();
        armeeManager = new ArmeeManager();
        raidManager = new RaidManager();
        dailyQuest = new DailyQuest();
        prisonManager = new PrisonManager();
        Bukkit.getPluginManager().registerEvents(prisonManager, this);
        getCommand("taeglichequest").setExecutor(dailyQuest);
        Bukkit.getPluginManager().registerEvents(dailyQuest, this);
        getCommand("killalltroops").setExecutor(new KillAllTroopsCommand());
        Bukkit.getPluginManager().registerEvents(raidManager, this);
        getCommand("provocateraid").setExecutor(new ProvocateRaidCommand());
        MobListCommand mobListCommand = new MobListCommand();
        LandMobsCommand landMobsCommand = new LandMobsCommand();
        getCommand("landmobs").setExecutor(landMobsCommand);
        getCommand("moblist").setExecutor(mobListCommand);
        getCommand("landpunkte").setExecutor(new GetPointsFromLand());
        getCommand("endraid").setExecutor(new EndRaid());
        Bukkit.getPluginManager().registerEvents(landMobsCommand, this);
        Bukkit.getPluginManager().registerEvents(mobListCommand, this);
        Bukkit.getPluginManager().registerEvents(new AllTheMobsManager(), this);
        Bukkit.getPluginManager().registerEvents(armeeManager, this);
        Bukkit.getPluginManager().registerEvents(landRoleManager, this);
        Bukkit.getPluginManager().registerEvents(landTagManager, this);
        Bukkit.getPluginManager().registerEvents(landManager, this);
        Bukkit.getPluginManager().registerEvents(provinceManager, this);
        Bukkit.getPluginManager().registerEvents(homeManager, this);
        Bukkit.getPluginManager().registerEvents(new ProtectionListener(), this);
        getCommand("landtag").setExecutor(new LandTagCommand());
        getCommand("tagfarbe").setExecutor(new TagColorCommand());
        getCommand("setpresentationholo").setExecutor(new SetPresentationHoloLocationCommand());
        getCommand("coins").setExecutor(new CoinCommand());
        getCommand("befehle").setExecutor(new CommandsCommand());
        getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("chatclear").setExecutor(new ChatClearCommand());
        getCommand("enderchest").setExecutor(new EnderchestCommand());
        getCommand("gamemode").setExecutor(new GamemodeCommand());
        getCommand("hut").setExecutor(new HatCommand());
        getCommand("inventar").setExecutor(new InvseeCommand());
        getCommand("verzaubern").setExecutor(new MagicCommand());
        getCommand("umbennenen").setExecutor(new RenameCommand());
        Teleporter teleporter = new Teleporter();
        getCommand("teleporter").setExecutor(teleporter);
        Bukkit.getPluginManager().registerEvents(teleporter, this);
        AdminAFK adminAFK = new AdminAFK();
        getCommand("adminafk").setExecutor(adminAFK);
        Bukkit.getPluginManager().registerEvents(adminAFK, this);
        getCommand("signieren").setExecutor(new SignCommand());
        getCommand("tpa").setExecutor(new TpaCommand());
        getCommand("vanish").setExecutor(new VanishCommand());
        getCommand("auktionshaus").setExecutor(new AuctioneersCommand());
        getCommand("moebelhaus").setExecutor(new FurnitureCommand());
        getCommand("landeinladungannehmen").setExecutor(new LandInvitationAcceptCommand());
        getCommand("landeinladungablehnen").setExecutor(new LandInvitationDenyCommand());
        getCommand("tutorialnpc").setExecutor(new TutorialNPCCommand());
        getCommand("tutorialnein").setExecutor(new TutorialNoCommand());
        getCommand("starttutorial").setExecutor(new StartTutorialCommand());
        getCommand("markttutorial").setExecutor(new MarktTutorial());
        getCommand("createlandfast").setExecutor(new FastLandCreationCommand());
        getCommand("marktnein").setExecutor(new MarktNoCommand());
        getCommand("rtp").setExecutor(new RtpCommand());
        getCommand("startmehrinfos").setExecutor(new StartMoreInfos());
        SneakChallenge sneakChallenge = new SneakChallenge();
        Bukkit.getPluginManager().registerEvents(sneakChallenge, this);
        getCommand("sprung").setExecutor(new SneakChallenge());
        getCommand("forschungsergebnis").setExecutor(new ResearchResultCommand());
        getCommand("startvoteday").setExecutor(new StartVoteDayCommand());
        getCommand("tagja").setExecutor(new VoteDayYesCommand());
        getCommand("tagnein").setExecutor(new VoteDayNoCommand());
        getCommand("booster").setExecutor(boosterManager);

        getCommand("message").setExecutor(new MsgCommand(replyMap));
        getCommand("reply").setExecutor(new ReplyCommand(replyMap));
        Bukkit.getPluginManager().registerEvents(boosterManager, this);
        Bukkit.getPluginManager().registerEvents(new CaptainAmericaShield(), this);
        Bukkit.getPluginManager().registerEvents(new MainListener(), this);
        Bukkit.getPluginManager().registerEvents(new MarketCommand(), this);
        Bukkit.getPluginManager().registerEvents(new EnderchestListener(), this);
        Bukkit.getPluginManager().registerEvents(new HawkEyeBow(), this);
        Bukkit.getPluginManager().registerEvents(new JetPack(), this);
        Bukkit.getPluginManager().registerEvents(new Mjölnir(), this);
        Bukkit.getPluginManager().registerEvents(new NetShooter(), this);
        Bukkit.getPluginManager().registerEvents(new Pistol(), this);
        Bukkit.getPluginManager().registerEvents(new Wolverine(), this);
        Bukkit.getPluginManager().registerEvents(new Goverment(), this);
        Bukkit.getPluginManager().registerEvents(new TownHall(), this);
        Bukkit.getPluginManager().registerEvents(new CityBlock(), this);
        Bukkit.getPluginManager().registerEvents(auctioneers, this);
        Bukkit.getPluginManager().registerEvents(landManager, this);
        Bukkit.getPluginManager().registerEvents(new FurnitureListener(), this);
        Bukkit.getPluginManager().registerEvents(new TutorialListener(), this);
        Bukkit.getPluginManager().registerEvents(new HeroKeaListener(), this);
        Bukkit.getPluginManager().registerEvents(new CaseOpeningListener(), this);

        getCommand("trust").setExecutor(new TrustCommand());
        getCommand("semitrust").setExecutor(new SemiTrustCommand());
        getCommand("untrust").setExecutor(new UntrustCommand());
        getCommand("semiuntrust").setExecutor(new SemiUnTrustCommand());

        BirthdayCommand birthdayCommand = new BirthdayCommand();
        getCommand("birthday").setExecutor(birthdayCommand);
        Bukkit.getPluginManager().registerEvents(birthdayCommand, this);

        getCommand("setcasewinnings").setExecutor(new SetCaseOpeningCommand(this));

        CraftingRecipeCommand craftingRecipeCommand = new CraftingRecipeCommand();
        getCommand("home").setExecutor(new HomeCommand());
        getCommand("rezepte").setExecutor(craftingRecipeCommand);
        Bukkit.getPluginManager().registerEvents(craftingRecipeCommand, this);
        LandCreator landCreator = new LandCreator();
        getCommand("createland").setExecutor(landCreator);
        getCommand("mentor").setExecutor(new MentorCommand());
        Bukkit.getPluginManager().registerEvents(new MentorListener(), this);
        Bukkit.getPluginManager().registerEvents(landCreator, this);
        LandGUI landGUI = new LandGUI();
        getCommand("land").setExecutor(landGUI);
        Bukkit.getPluginManager().registerEvents(landGUI, this);
        LandShop landShop = new LandShop();
        getCommand("landshop").setExecutor(landShop);
        Bukkit.getPluginManager().registerEvents(landShop, this);
        insuranceManager = new InsuranceManager();
        Bukkit.getPluginManager().registerEvents(insuranceManager, this);
        InsuranceGui insuranceGui = new InsuranceGui();
        getCommand("landvote").setExecutor(new VoteForLandCommand());
        getCommand("versicherungen").setExecutor(insuranceGui);
        Bukkit.getPluginManager().registerEvents(insuranceGui, this);
        initChallenges();
        getCommand("dimensions").setExecutor(new DimensionCommand());
        Bukkit.getPluginManager().registerEvents(new DarkStick(this), this);
        Bukkit.getPluginManager().registerEvents(new NatureSword(), this);
        Bukkit.getPluginManager().registerEvents(new Sandstorm(), this);
        DimensionStorage dimensionStorage = new DimensionStorage();
        getCommand("dimensionstorage").setExecutor(dimensionStorage);
        Bukkit.getPluginManager().registerEvents(dimensionStorage, this);
        TutorialVideo tutorialVideo = new TutorialVideo();
        getCommand("createtutorialvideo").setExecutor(tutorialVideo);
        Bukkit.getPluginManager().registerEvents(tutorialVideo, this);
        getCommand("createcity").setExecutor(new CreateCityCommand());
        Bukkit.getPluginManager().registerEvents(new SpecialItemsListener(), this);
        getCommand("besondereitems").setExecutor(new SpecialItemsCommand());
        Bukkit.getPluginManager().registerEvents(new DuckStick(), this);
        getCommand("prison").setExecutor(new PrisonCommand());
        getCommand("freeprison").setExecutor(new FreePrisonCommand());
        getCommand("setprisonspawn").setExecutor(new SetPrisonSpawnPointCommand());
        getCommand("farmwelt").setExecutor(new FarmworldCommand());

        Bukkit.getPluginManager().registerEvents(new ScaleStick(), this);


        new FishMarketShop();
        new WoodMarketShop();
        new MinerMarketShop();
        new ButcherMarketShop();
        new ArmorerMarketShop();
        new MonsterDropMarketShop();
        new AnimalMarketShop();
        new FloristMarketShop();

        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                /*superheroManager = new SuperheroManager();
                Bukkit.getPluginManager().registerEvents(superheroManager, HeroCraft.getPlugin());
                landPresentationManager = new LandPresentationManager();
                Bukkit.getPluginManager().registerEvents(landPresentationManager, plugin);*/
                dimensionManager = new DimensionManager();
                Bukkit.getPluginManager().registerEvents(dimensionManager, plugin);
            }
        }, 20*2);
    }


    public static ItemStack getItemsAdderItem(String itemName) {
        ItemStack itemStack = null;
        for(CustomStack i : ItemsAdder.getAllItems()) {
            if(i.getDisplayName().equalsIgnoreCase(itemName)) {
                itemStack = i.getItemStack();
            }
        }
        return itemStack;
    }

    public void initChallenges() {
        saveConfig();
        openGUICommand = new Commands();
        createGUI = new CreateGui();
        guiListener = new GuiListener();
        createConfig = new CreateConfigs();
        getCommand("challenge").setExecutor(openGUICommand);
        Bukkit.getPluginManager().registerEvents((Listener)guiListener, (Plugin)this);
        openGUICommand.reloadConfig(null);
    }

    @Override
    public void onDisable() {
        for(Raid i : raidManager.getRaids()) {
            i.killAllRaidEntities();
            i.finishRaidFailed();
        }
        landPresentationManager.richestHolo.delete();
        landPresentationManager.bestHolo.delete();
        Bukkit.getScheduler().cancelTask(dimensionManager.getTaskID());
        Bukkit.getScheduler().cancelTask(dimensionManager.getTaskID2());
    }

    public static HeroCraft getPlugin() {
        return plugin;
    }

    public Coin getCoin() {
        return coin;
    }
    public MySQL getMySQL() {
        return mySQL;
    }
    public Auctioneers getAuctioneers() {
        return auctioneers;
    }
    public LandManager getLandManager() {
        return landManager;
    }

    public DimensionManager getDimensionManager() {
        return dimensionManager;
    }

    public SuperheroManager getSuperheroManager() {
        return superheroManager;
    }

    public InsuranceManager getInsuranceManager() {
        return insuranceManager;
    }

    public VoteDayManager getVoteDayManager() {
        return voteDayManager;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public DungeonManager getDungeonManager() {
        return dungeonManager;
    }

    public MySQL getShopMySQL() {
        return shopMySQL;
    }

    public ProvinceManager getProvinceManager() {
        return provinceManager;
    }

}