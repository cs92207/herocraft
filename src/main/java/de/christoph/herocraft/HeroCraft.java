package de.christoph.herocraft;

import de.christoph.herocraft.afksystem.AdminAFK;
import de.christoph.herocraft.armee.ResearchResultCommand;
import de.christoph.herocraft.armee.SuperheroManager;
import de.christoph.herocraft.basiccommands.*;
import de.christoph.herocraft.birthday.BirthdayCommand;
import de.christoph.herocraft.caseopening.CaseOpeningListener;
import de.christoph.herocraft.caseopening.SetCaseOpeningCommand;
import de.christoph.herocraft.challenges.ChallengeManager;
import de.christoph.herocraft.challenges.ChangeChallengeCommand;
import de.christoph.herocraft.dimensions.DimensionCommand;
import de.christoph.herocraft.dimensions.DimensionManager;
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
import de.christoph.herocraft.lands.province.CityBlock;
import de.christoph.herocraft.lands.province.ProvinceManager;
import de.christoph.herocraft.lands.province.TownHall;
import de.christoph.herocraft.market.*;
import de.christoph.herocraft.market.herokea.HeroKeaListener;
import de.christoph.herocraft.mysql.MySQL;
import de.christoph.herocraft.protection.ProtectionListener;
import de.christoph.herocraft.protection.SneakChallenge;
import de.christoph.herocraft.school.MentorCommand;
import de.christoph.herocraft.school.MentorListener;
import de.christoph.herocraft.school.skills.SkillManager;
import de.christoph.herocraft.specialitems.*;
import de.christoph.herocraft.status.Status;
import de.christoph.herocraft.teleporter.Teleporter;
import de.christoph.herocraft.tutorial.*;
import de.christoph.herocraft.voteday.StartVoteDayCommand;
import de.christoph.herocraft.voteday.VoteDayManager;
import de.christoph.herocraft.voteday.VoteDayYesCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;

public final class HeroCraft extends JavaPlugin {

    private static HeroCraft plugin;
    private ChallengeManager challengeManager;
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
    // private DimensionManager dimensionManager;

    @Override
    public void onEnable() {
        plugin = this;
        mySQL = new MySQL("45.135.201.157", 3306, "herocraft", "SV-Studios", "1Emanuel0602#");
        shopMySQL = new MySQL("45.135.201.157", 3306, "shop", "SV-Studios", "1Emanuel0602#");
        coin = new Coin();
        auctioneers = new Auctioneers();
        landManager = new LandManager();
        challengeManager = new ChallengeManager();
        voteDayManager = new VoteDayManager();
        skillManager = new SkillManager();
        homeManager = new HomeManager();
        dungeonManager = new DungeonManager();
        provinceManager = new ProvinceManager();
        Bukkit.getPluginManager().registerEvents(landManager, this);
        Bukkit.getPluginManager().registerEvents(provinceManager, this);
        Bukkit.getPluginManager().registerEvents(homeManager, this);
        Status status = new Status();
        Bukkit.getPluginManager().registerEvents(status, this);
        getCommand("status").setExecutor(status);
        Bukkit.getPluginManager().registerEvents(new ProtectionListener(), this);
        getCommand("changechallenge").setExecutor(new ChangeChallengeCommand());
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
        getCommand("ruestungsschmied").setExecutor(new ArmoursmithCommand());
        getCommand("schlachter").setExecutor(new ButcherCommand());
        getCommand("fischer").setExecutor(new FisherCommand());
        getCommand("bergarbeiter").setExecutor(new MinerCommand());
        getCommand("holzfaeller").setExecutor(new WoodCommand());
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
        getCommand("dekorationsshop").setExecutor(new DecorationShopCommand());
        getCommand("forschungsergebnis").setExecutor(new ResearchResultCommand());
        getCommand("startvoteday").setExecutor(new StartVoteDayCommand());
        getCommand("tagja").setExecutor(new VoteDayYesCommand());
        getCommand("tagnein").setExecutor(new VoteDayYesCommand());
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
        Bukkit.getPluginManager().registerEvents(new ArmoursmithListener(), this);
        Bukkit.getPluginManager().registerEvents(new ButcherListener(), this);
        Bukkit.getPluginManager().registerEvents(new FisherListener(), this);
        Bukkit.getPluginManager().registerEvents(new MinerListener(), this);
        Bukkit.getPluginManager().registerEvents(new WoodListener(), this);
        Bukkit.getPluginManager().registerEvents(new Goverment(), this);
        Bukkit.getPluginManager().registerEvents(new TownHall(), this);
        Bukkit.getPluginManager().registerEvents(new CityBlock(), this);
        Bukkit.getPluginManager().registerEvents(auctioneers, this);
        Bukkit.getPluginManager().registerEvents(landManager, this);
        Bukkit.getPluginManager().registerEvents(new FurnitureListener(), this);
        Bukkit.getPluginManager().registerEvents(new DecorationShopListener(), this);
        Bukkit.getPluginManager().registerEvents(new TutorialListener(), this);
        Bukkit.getPluginManager().registerEvents(new HeroKeaListener(), this);
        Bukkit.getPluginManager().registerEvents(new CaseOpeningListener(), this);

        getCommand("trust").setExecutor(new TrustCommand());
        getCommand("untrust").setExecutor(new UntrustCommand());

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
        //getCommand("dimensions").setExecutor(new DimensionCommand());
        Bukkit.getScheduler().scheduleSyncDelayedTask(HeroCraft.getPlugin(), new Runnable() {
            @Override
            public void run() {
                superheroManager = new SuperheroManager();
                Bukkit.getPluginManager().registerEvents(superheroManager, HeroCraft.getPlugin());
                landPresentationManager = new LandPresentationManager();
                Bukkit.getPluginManager().registerEvents(landPresentationManager, plugin);
                //dimensionManager = new DimensionManager();
                //Bukkit.getPluginManager().registerEvents(dimensionManager, plugin);
            }
        }, 20*2);
    }

    @Override
    public void onDisable() {
        landPresentationManager.richestHolo.delete();
        landPresentationManager.bestHolo.delete();
        //Bukkit.getScheduler().cancelTask(dimensionManager.getTaskID());
    }

    public ChallengeManager getChallengeManager() {
        return challengeManager;
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

    //public DimensionManager getDimensionManager() {
        //return dimensionManager;
    //}

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