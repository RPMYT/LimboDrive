import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TextParsingException extends RuntimeException {
    public TextParsingException(String message) {
        super(message);
    }
}

record Block(String label, ArrayList<int[]> contents) {}

class Testbed {
    public static Map<String, Function<String, String>> commands = new HashMap<>();

    public static Map<Integer, Block> blocks = new LinkedHashMap<>();
    public static Map<String, Integer> addresses = new LinkedHashMap<>();

    public static int[] textToBytes(String text, int max) {
        int[] bytes = new int[max];

        int pos = 0;

        while (pos < text.length()-1) {
            char there = text.charAt(pos);

            if (there == '[') {
                int matchingPos = pos;
                try {
                    while (text.charAt(matchingPos) != ']') {
                        matchingPos++;
                    }
                } catch (StringIndexOutOfBoundsException exception) {
                    throw new InputMismatchException("String contains '[' at postiion " + pos + " but no subsequent ']': " + text);
                }

                pos++;
                try {
                    byte[] parsed = java.util.HexFormat.ofDelimiter(" ").parseHex(text, pos, matchingPos);

                    for (int index = 0; index < parsed.length; index++) {
                        if (parsed[index] < 0) {
                            bytes[index + pos] = parsed[index] & 0xFF;
                            //System.out.println("Hex Character (During Parse): " + (char) (parsed[index] & 0xFF) + ", Hex Value (During Parse): " + (parsed[index] & 0xFF));
                        } else {
                            bytes[index + pos] = parsed[index];
                            //System.out.println("Hex Character (During Parse): " + (char) (parsed[index]) + ", Hex Value (During Parse): " + (parsed[index]));
                        }
                    }

                    pos++;
                } catch (NumberFormatException exception) {
                    continue;
                }

//                for (int ignored = 2; ignored < matchingPos; ignored+=2) {
//                    if (pos+2 >= text.length()) {
//                        break;
//                    }
//
//                    String sub = text.substring(pos, pos+2);
//                    if (sub.endsWith("]")) {
//                        sub = text.substring(pos, (pos+1));
//                    }
//
//                    if (sub.startsWith("[")) {
//                        sub = text.substring(pos+1, (pos + 3));
//                    }
//
//                    if (sub.charAt(sub.length()-2) == ']') {
//                        break;
//                    }
//                }

                pos = matchingPos;
            } else {
                //System.out.println("Character (During Parse): " + (int) there + ", Value (During Parse): " + there);

                bytes[pos] = there;
            }

            pos++;
        }

        return (pos < max ? Arrays.copyOf(bytes, pos) : bytes);
    }

    public static int[][] compileBlock(String block) {
        Stream<String> lines = block.lines();
        AtomicReference<Block> current = new AtomicReference<>(null);
        AtomicReference<Block> previous = new AtomicReference<>(null);

        int count = (int) block.lines().count();
        int[][] texts = new int[count][];

        AtomicInteger pos = new AtomicInteger();
        AtomicInteger addr = new AtomicInteger(0xC00000);
        System.out.println("Parsing: ");
        lines.forEach(line -> {
            if (line.endsWith(":")) {
                if (previous.get() != null) {
                    int curAddr = addr.get();
                    for (int[] content : previous.get().contents()) {
                        curAddr += content.length-1;
                    }

                    blocks.put(curAddr + 1, previous.get());
                    addr.set(curAddr + 1);
                    addresses.put(current.get().label(), curAddr);
                }

                current.set(new Block(
                        line.replace(":", ""),
                        new ArrayList<>()
                ));
                previous.set(current.get());
            } else {
                int[] parsed = textToBytes(
                        line.replace("\"", ""),
                        line.length() - 1
                );
                //System.out.print("\n");
                texts[pos.get()] = parsed;
                for (int intgr : parsed) {
                    //System.out.println("Character (After Parse): " + (char) intgr + ", Value (After Parse): " + intgr);
                }

                Block curr = current.get();
                curr.contents().add(parsed);
                current.set(curr);
            }
            pos.getAndIncrement();

            if (pos.get() == count) {
                int curAddr = addr.get();
                for (int[] content : previous.get().contents()) {
                    curAddr += content.length-1;
                }

                blocks.put(curAddr, current.get());
                addresses.put(current.get().label(), curAddr);
            }
        });
        return texts;
    }

    public static void parseBlock(int[] text) throws IOException {
        boolean finished = false;

        boolean parsingByte = false;
        boolean parsingShort = false;
        boolean parsingLong = false;

        int parsedByte = 0;
        short parsedShort = 0;
        int parsedLong = 0;

        int[] longComponents = new int[3];
        int[] shortComponents = new int[2];

        int parsingStage = 0;

        boolean jumping = false;
        boolean shouldReturn = true;

        for (int there : text) {
            if (finished) {
                return;
            }

            if (parsingLong) {
                longComponents[parsingStage] = there;
                //System.out.println("Component " + parsingStage + ": " + there);
                if (parsingStage == 2) {
                    parsedLong = (longComponents[2] & 0xFF) | ((longComponents[1] & 0xFF) << 8) | ((longComponents[0] & 0x0F) << 16);
                    //System.out.println("Parsed: 0x" + Integer.toHexString(parsedLong));
                    parsingLong = false;
                    continue;
                }
                parsingStage++;
                continue;
            }

            switch (there) {
                case 0, 1 -> System.out.print("\n");
                case 2 -> finished = true;
                case 3 -> {
                    System.out.print(" ▼");
                    System.in.read();
                    System.out.print("\n");
                }

                case 0x13 -> {
                    System.in.read();
                    System.out.print("\n");
                }

                case 0x14 -> {
                    System.out.print(" ▼");
                    System.in.read();
                }

                case 0x12 -> System.out.print("\r");

                case 0x0A -> {
                    //System.out.println("Beginning jump");
                    parsingLong = true;
                    jumping = true;
                    shouldReturn = false;
                }

                case 0x08 -> {
                    parsingLong = true;
                    jumping = true;
                    shouldReturn = true;
                }

                default -> System.out.print((char) there);
            }

            if (jumping) {
                if (parsedLong != 0) {
                    ArrayList<int[]> contents = blocks.get(parsedLong + 0xC00000).contents();
                    //System.out.println("Jumping!");
                    for (int[] content : contents) {
                        parseBlock(content);
                    }
                    parsedLong = 0;
                    parsingLong = false;
                    finished = !shouldReturn;
                }
            }
        }
    }

    public static String parseCommand(String input) {
        String target = input;
        for (String key : commands.keySet()
                .stream()
                .filter(input::matches)
                .collect(Collectors.toSet())
        ) {
            if (input.matches(key)) {
                String parsed = commands.getOrDefault(key, s -> input).apply(input);
                if (!Objects.equals(parsed, input)) {
                    target = parsed;
                }
            }
        }
        return target;
    }

    public static void main(String[] args) throws IOException {
        compileBlock("pie:\nHello; have we met?[13 02]\nYou wanna pie?[03]\nlame:\nThis bakery is so lame.[03]\nnpc:\nHello there![01]\nI am an NPC![03]\nThis text is[14][12]gone![02]\njumps:\ncall(pie)\ncall(lame)\ngoto(npc)");
        //System.out.print("\n");
        blocks.forEach((addr, block) -> {
            System.out.println("Have block '" + block.label() + "' at address 0x" + Integer.toHexString(addr));
            for (int[] content : block.contents()) {
                for (int intgr : content) {
                    //System.out.print((char) intgr);
                }
            }
        });

        System.out.print(parseCommand("goto(pie)"));
        //parseBlock(textToBytes("[0A 00 00 9C]", 256));
    }

    static {
        commands.put("end", s -> "[13 02]");
        commands.put("wait", s-> "[13]");
        commands.put("next", s -> "[03 00]");
        commands.put("goto\\(.*\\)", s -> {
            String label = s
                    .replace("goto", "")
                    .replace("(", "")
                    .replace(")", "");
            if (!addresses.containsKey(label)) {
                System.out.println(label);
                throw new TextParsingException("");
            } else {
                String address = Integer.toHexString(addresses.get(label));
                return ("[0A " + address.substring(0, 2) + " " + address.substring(2, 4) + " " + address.substring(4) + "]").toUpperCase();
            }
        });

        commands.put("call\\(.*\\)", commands.get("goto\\(.*\\)"));
    }
}