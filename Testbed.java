import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;

class TextParsingException extends RuntimeException {
    public TextParsingException(String message) {
        super(message);
    }
}

record Block(String label, ArrayList<int[]> contents) {}

@SuppressWarnings("unused")
enum ParamType {
    BYTE,
    SHORT,
    LONG
}

record Command(String name, String regex, char terminator, Function<String, String> parser, ParamType... parameters) {}

@SuppressWarnings("unused")
class Testbed {
    public static Map<String, Command> COMMANDS = new HashMap<>();

    public static Map<Integer, Block> BLOCK_DATABASE = new LinkedHashMap<>();
    public static Map<String, Integer> ADDRESS_MAPPINGS = new LinkedHashMap<>();

    public static final int MAX_LABEL_LENGTH = 20;

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

                pos = matchingPos;
            } else {
                //System.out.println("Character (During Parse): " + (int) there + ", Value (During Parse): " + there);

                bytes[pos] = there;
            }

            pos++;
        }

        return (pos < max ? Arrays.copyOf(bytes, pos) : bytes);
    }

    public static int[] compileBlock(String block) {
        Stream<String> lines = block.lines();
        AtomicReference<Block> current = new AtomicReference<>(null);
        AtomicReference<Block> previous = new AtomicReference<>(null);

        ArrayList<Integer> compiled = new ArrayList<>();

        int count = (int) block.lines().count();

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

                    BLOCK_DATABASE.put(curAddr + 1, previous.get());
                    addr.set(curAddr + 1);
                    ADDRESS_MAPPINGS.put(current.get().label(), curAddr+1);
                }

                current.set(new Block(
                        line.replace(":", "").substring(0, (Math.min(line.length() - 1, MAX_LABEL_LENGTH))),
                        new ArrayList<>()
                ));
                previous.set(current.get());
            } else {
                int[] parsed = textToBytes(
                        line.replace("\"", ""),
                        line.length() - 1
                );

                for (int piece : parsed) {
                    compiled.add(piece);
                }

                if (current.get() != null) {
                    Block curr = current.get();
                    curr.contents().add(parsed);
                    current.set(curr);
                }
            }
            pos.getAndIncrement();

            if (pos.get() == count) {
                int curAddr = addr.get();
                if (previous.get() != null) {
                    for (int[] content : previous.get().contents()) {
                        curAddr += content.length - 1;
                    }

                    BLOCK_DATABASE.put(curAddr, current.get());
                    ADDRESS_MAPPINGS.put(current.get().label(), curAddr);
                }
            }
        });

        int[] toReturn = new int[compiled.size()];
        for (int index = 0; index < compiled.size(); index++) {
            toReturn[index] = compiled.get(index);
        }
        return toReturn;
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
                    parsingStage = 0;
                    continue;
                }
                parsingStage++;
                continue;
            }

            switch (there) {
                case 1 -> System.out.print("\n");
                case 2 -> finished = true;
                case 3 -> {
                    System.out.print(" ▼");
                    System.in.read();
                    System.out.print("\n");
                }

                case 0x13 -> System.in.read();

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
                    //System.out.println(Integer.toHexString(parsedLong + 0xC00000));
                    ArrayList<int[]> contents = BLOCK_DATABASE.get(parsedLong + 0xC00000).contents();
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
    public static String preparse(String script) {
        int pos = 0;

        StringBuilder parsed = new StringBuilder();
        while (pos < script.length()) {
            for (Map.Entry<String, Command> entry : COMMANDS.entrySet()) {
                Command command = entry.getValue();

                if (pos >= script.length()) {
                    break;
                }

                //System.out.println("Sub: " + script.substring(pos, pos + Math.min(script.length()-1, command.name().length())) + ", Name: " + command.name() + ", Matches: " + script.substring(pos, pos + Math.min(script.length()-1, command.name().length())).matches(command.name()));

                if (script.substring(pos, pos + Math.min(script.length()-1, command.name().length())).matches(command.name())) {
                    //System.out.println("Match found");
                    int length = command.name().length();
                    for (int step = pos; step < script.length(); step += length) {
                        //System.out.println("Step: " + step + ", Pos: " + pos);
                        try {
                            int seek = pos;

                            try {
                                while (script.charAt(seek) != command.terminator()) {
                                    seek++;
                                }

                                String part = script.substring(pos, seek).strip();
                                String replaced = command.parser().apply(part);
                                parsed.append(replaced);

                                pos += (seek - pos) + 1;
                                break;
                            } catch (StringIndexOutOfBoundsException exception) {
                                exception.printStackTrace();
                                throw new TextParsingException("Could not find command terminator!");
                            }
                        } catch (StringIndexOutOfBoundsException ignored) {}
                    }
                }
            }

            parsed.append(script, Math.min(pos, script.length()-1), Math.min(script.length()-1, pos + 1));
            pos++;
        }

        return parsed.toString();
    }

    public static void addCommand(String command, String regex, Function<String, String> parser, String code, ParamType... parameters) {
        COMMANDS.put(command, new Command(
            command,
            regex == null ? command : regex,
            parameters.length == 0 ? command.charAt(command.length()-1) : ')',
            parser == null ? input -> code : parser
        ));
    }

    public static void main(String[] args) throws IOException {
        compileBlock(preparse("pie:\nHello; have we met? next \nYou wanna pie? end \nlame:\nThis bakery is so lame. end\nnpc:\nHello there!\nI am an NPC! next\nThis text is wait[12]gone! end\n"));

        String preparsed = preparse("Time to jump! call(pie) goto(npc)");
        int[] compiled = compileBlock(preparsed);
        parseBlock(compiled);

        //parseBlock(textToBytes("[0A 00 00 9C]", 256));
    }

    static {
        addCommand("end", null, null, "[13 02]");
        addCommand("wait", null, null, "[13]");
        addCommand("next", null, null, "[03 01]");

        addCommand("goto", ".{1," + MAX_LABEL_LENGTH + "}", s -> {
            String label = s
                .replace("goto", "")
                .replace("(", "")
                .replace(")", "");
            label = label.substring(0, Math.min(label.length(), MAX_LABEL_LENGTH - 1));

            if (! ADDRESS_MAPPINGS.containsKey(label)) {
                System.out.println(label);
                throw new TextParsingException("Label '" + label + "' does not exist!");
            } else {
                String address = Integer.toHexString(ADDRESS_MAPPINGS.get(label));
                //System.out.println(ret);
                return ("[0A " + address.substring(0, 2) + " " + address.substring(2, 4) + " " + address.substring(4) + "]").toUpperCase();
            }
        }, null, ParamType.LONG);

        addCommand("call", ".{1," + MAX_LABEL_LENGTH + "}", s -> {
            String label = s
                .replace("call", "")
                .replace("(", "")
                .replace(")", "");

            label = label.substring(0, Math.min(label.length(), MAX_LABEL_LENGTH - 1));
            if (! ADDRESS_MAPPINGS.containsKey(label)) {
                System.out.println(label);
                throw new TextParsingException("Label '" + label + "' does not exist!");
            } else {
                String address = Integer.toHexString(ADDRESS_MAPPINGS.get(label));
                //System.out.println(ret);
                return ("[08 " + address.substring(0, 2) + " " + address.substring(2, 4) + " " + address.substring(4) + "]").toUpperCase();
            }
        }, null, ParamType.LONG);
    }
}