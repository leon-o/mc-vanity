package top.leonx.vanity.util;

import java.util.Random;

public class NameGenerator {
    public static final String[] part1={
      "Fo","Go","Sur","De","Ca","Wi","Por","Vul","Le","An","Ho","App","Mor","Jo","Jon","Ba","Kur","Ku",
            "Wi","Su","Ni"
    };
    public static final String[] part2={
            "ir","lan","bin","ba","li","an","on","mon","son","can","con"
    };
    public static final String[] part3={
            "ly","da","tron","er","na","hub","ge","lian"
    };

    public static String getRandomName(Random rand)
    {
        if (rand.nextDouble()>0.5) {
            return part1[rand.nextInt(part1.length)]+part2[rand.nextInt(part2.length)];
        }else{
            return part1[rand.nextInt(part1.length)]+part2[rand.nextInt(part2.length)]+part3[rand.nextInt(part3.length)];
        }
    }
}
