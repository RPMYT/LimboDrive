package limbo.drive.starfield;

import limbo.drive.LimboDrive;
import limbo.drive.starfield.data.Star;
import limbo.drive.util.StringHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class StarfieldGenerator {
    public static final HashMap<Integer, HashMap<Integer, HashMap<Integer, Star>>> STARS = new HashMap<>();

    public static class Util {
        private static final String[] prefixes = new String[]{
            "Nona", "Tetra", "Centi", "Alpha", "Beta", "Gamma", "Delta", "Eta", "Kappa", "Xi", "Omega", "Sigma", "Tau", "Omicron", "Lambda", "Anti", "Arch", "Bore", "Eco", "Endo", "Exo", "Geo", "Grand", "Bare", "Long", "Non", "Super", "Meta", "Semi", "Sub", "Under"
        };
        
        private static final String[] middles = new String[]{
            "Felis", "Mali", "Arch'tol", "Apillaris", "Agamir", "Asphylium", "Atryll", "Balartropheus", "Bubobubo", "Brynox", "Borrphylus", "Bixion", "Crom'atar", "Clorr'barad", "Cindrol", "Chogal", "Coxinthropus", "Diomochtus", "Diatomica", "Drollgat", "Daorshar", "Dyshor", "Elunemar", "Ekshatar", "Ensavrithal", "Ersix", "Echyon", "Folchost", "Felxis", "Fuzortrach", "Fom'trox", "Fyyyr", "Gobrogloctus", "Grontosh'tar", "GORG-KRLN*", "Gytropia", "Grogotal", "Hydraxion", "Hilisorn", "Hacir'soron", "Hoebrond", "Hackar'soron", "Illurius", "Infernogerm", "Ichtiopar", "Indrol'soron", "Iatreziol", "Jarak", "Jyraxor", "Jissar", "Joltonar", "Japragal", "Kollotrops", "Khol'grob", "Kuzdulnar", "Korffugium", "Kirrotarr", "Luk'galesh", "Lon", "Libtal", "Lach'galesh", "Lambrosarchus", "Mul'tesh", "Marradral", "Mesjur", "Mentrasza", "Murrtrunax", "Nebulus", "Neolucidius", "Nomenara", "Nijgar", "Nachystral", "Orgathol", "Orbiculatus", "Oburrus", "Ondol", "Ojdra'sul", "Pak'tak", "Pholotrox", "Patrick'Stuwartelur", "Pikaiar", "Proloct", "Q'chan", "Quantylychinus", "Qor'jabal", "Qovojdran", "Qolirn", "Ryyyk'trakar", "Rhexioculus", "Rallbhar", "Robarmaghaton", "Rajckatror", "Silurius", "Scyngrolitis", "Sacharr", "Sol", "Sorr'soron", "Tallicus", "TK-421*", "Turmidi", "Tysaurannorus", "Taucar", "Uchval", "Utiopia", "Undul", "Uxixil", "Umbrajil", "Valarius", "Vermithrex", "Vulpisar", "Vylnar", "Vorgrosar", "Wonxin", "Waj", "Wros", "Wipon", "Waf'bar", "Xizorat", "Xailyr", "Xozburdum", "Xi'ci", "Xiab'yoj", "Ynn'zagar", "Yilser", "Yombadrax", "Yj'baj", "Yuuzhan'Vong", "Zablator", "Zuxinotelus", "Zubebonaraki", "Zoravas", "Zangrom"
        };

        private static final String[] suffixes = new String[]{
            "Prime", "Septim", "Auto", "-ing", "-er", "-en"
        };

        public static final List<String> NAMES;

        public static List<String> generateAvailableNames(int maxLength, float prefixChance, float sufixChance) {
            Random random = new Random();

            List<String> names = new ArrayList<>();

            String full;
            for (String middle : middles) {
                if (middle.length() <= maxLength) {
                    names.add(middle.replace("*", ""));
                }

                for (String prefix : prefixes) {
                    if (!middle.startsWith("*") && random.nextFloat() < prefixChance) {
                        full = StringHelper.addPrefix(middle, prefix);
                        if (full.length() <= maxLength) {
                            names.add(full.replace("*", ""));
                        }
                    }

                    for (String sufix : suffixes) {
                        if (!middle.endsWith("*") && random.nextFloat() < sufixChance) {
                            full = StringHelper.addSuffix(middle, sufix);
                            if (full.length() <= maxLength) {
                                names.add(full.replace("*", ""));
                            }
                        }

                        if (!middle.startsWith("*") && !middle.endsWith("*") && random.nextFloat() < prefixChance && random.nextFloat() < sufixChance) {
                            full = StringHelper.addPrefix(StringHelper.addSuffix(middle, sufix), prefix);
                            if (full.length() <= maxLength) {
                                names.add(full);
                            }
                        }
                    }
                }
            }

            Collections.shuffle(names, random);
            return names;
        }

        static {
            NAMES = generateAvailableNames(18, 1f, 0.8f);
        }
    }

    public static void initialize() {
        LimboDrive.LOGGER.info("Generating the starfield...");
        LimboDrive.LOGGER.info("This might take a while, so be patient!");
        Random random = new Random();

        int generated = 0;
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Star>>> quadrants = new HashMap<>();
        for (int quadrant = 1; quadrant <= 4; quadrant++) {
            HashMap<Integer, HashMap<Integer, Star>> clusters = new HashMap<>();
            for (int cluster = 1; cluster <= 35; cluster++) {
                HashMap<Integer, Star> stars = new HashMap<>();
                double mass = random.nextDouble(0.7, 2.3);

                for (int star = 1; star <= (Math.pow(mass, -2.35) * 100); star++) {
                    int index = random.nextInt(0, Util.NAMES.size());
                    double starMass = mass * random.nextDouble(0.7, 1.4);
                    stars.put(star, new Star(
                        Util.NAMES.get(index),
                        starMass,
                        5000 * Math.pow(starMass, 0.62) + 1000,
                        quadrant,
                        cluster,
                        star
                    ));
                    Util.NAMES.remove(index);
                    generated++;
                }
                clusters.put(cluster, stars);
            }
            quadrants.put(quadrant, clusters);
        }

        STARS.clear();
        STARS.putAll(quadrants);
        LimboDrive.LOGGER.info("Starfield generation complete!");
        LimboDrive.LOGGER.info("Total stars generated: " + generated);
    }

    static {
        for (int quadrantID = 4; quadrantID <= 4; quadrantID++) {
            HashMap<Integer, HashMap<Integer, Star>> quadrant = new HashMap<>();
            for (int clusterID = 1; clusterID <= 35; clusterID++) {
                HashMap<Integer, Star> cluster = new HashMap<>();
                quadrant.put(clusterID, cluster);
            }
            STARS.put(quadrantID, quadrant);
        }
    }
}