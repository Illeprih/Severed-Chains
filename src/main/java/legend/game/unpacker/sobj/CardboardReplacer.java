package legend.game.unpacker.sobj;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import legend.game.unpacker.FileData;
import legend.game.unpacker.PathNode;
import legend.game.unpacker.Replacement;
import legend.game.unpacker.Transformations;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class CardboardReplacer {
  private final Long2ObjectMap<String> namer;

  private CardboardReplacer(final Long2ObjectMap<String> namer) {
    this.namer = namer;
  }

  private String resolveHash(final long hash) {
    return this.namer.getOrDefault(hash, String.format("%016x", hash));
  }

  public static List<Replacement> get2dReplacements(final Long2ObjectMap<String> namer) {
    final CardboardReplacer replacer = new CardboardReplacer(namer);

    final List<Replacement> replacements = new ArrayList<>();

    // Fletz - introduction
    replacements.add(new Replacement("Unflatten Fletz - introduction", replacer::fletzIntroduction));
    // Fletz - return from Donau
    replacements.add(new Replacement("Unflatten Fletz - return from Donau", replacer::fletzReturnFromDonau));
    // Fletz - Sendoff to Rogue
    replacements.add(new Replacement("Unflatten Fletz - sendoff to Rogue", replacer::fletzSendoff2Rouge));

    return replacements;
  }

  private static @Nullable PathNode getToNode(final PathNode root, final String path) {
    final String[] parts = path.split("/");

    PathNode currentNode = root;

    for(final String part : parts) {
      currentNode = currentNode.children.get(part);

      if(currentNode == null) {
        return null;
      }
    }

    return currentNode;
  }

  private static void replaceMrgLines(
    final PathNode root,
    final Transformations transformations,
    final String path,
    final Int2ObjectMap<String> replacements
  ) {
    editMrgLines(root, transformations, path, lines ->
      replacements.forEach((line, value) ->
        lines.set(line, line + "=" + value)
      )
    );
  }

  private static void editMrgLines(final PathNode root, final Transformations transformations, final String path, final Consumer<List<String>> editor) {
    final @Nullable PathNode node = getToNode(root, path);

    if(node == null) {
      return;
    }

    final List<String> lines = new ArrayList<>(
      node.data
        .readFixedLengthAscii(0, node.data.size())
        .lines()
        .toList()
    );

    editor.accept(lines);

    final String replaced = String.join(System.lineSeparator(), lines);
    transformations.replaceNode(
      node,
      new FileData(replaced.getBytes(StandardCharsets.US_ASCII))
    );
  }

  private void fletzIntroduction(
    final PathNode root,
    final Transformations transformations,
    final Set<String> flags
  ) {
    final String shopkeeper = "/sobj/" + this.resolveHash(0xfec05a2adaeea41cL);
    final String blueDress = "/sobj/" + this.resolveHash(0x7368841395eef392L);

    replaceMrgLines(
      root,
      transformations,
      "SECT/DRGN22.BIN/1145/mrg",
      new Int2ObjectOpenHashMap<>(Map.of(
        264, shopkeeper + "/model;8944", // Shopkeeper
        265, shopkeeper + "/animations/" + this.resolveHash(0xedf1bd6c2c01682aL) + ";52", // Standing still animation
        396,"@297;8944", // Bridge pacer model
        397,"@298;1816", // Standing still animation
        398,"@299;1816", // Walking animation
        429,blueDress + "/model;8944", // Blue dress
        430,blueDress + "/animations/" + this.resolveHash(0xffc5225ab65e1820L) + ";52", // Standing still animation
        435, blueDress + "/animations/" + this.resolveHash(0x052589f12db92391L) + ";52" // Interaction animation
      ))
    );

    replaceMrgLines(
      root,
      transformations,
      "SECT/DRGN22.BIN/1145/textures/mrg",
      new Int2ObjectArrayMap<>(Map.of(
        8, shopkeeper + "/textures/" + this.resolveHash(0x6526741a72c48321L) + ";4640",
        12, "@9;4640", // Bridge pacer
        13, blueDress + "/textures/" + this.resolveHash(0x82e2cf59cecad171L) + ";4640"
      ))
    );
  }

  private void fletzReturnFromDonau(
    final PathNode root,
    final Transformations transformations,
    final Set<String> flags
  ) {
    final String bridgePacer = "/sobj/" + this.resolveHash(0xe1c5eee77857cf91L);
    final String blueDress = "/sobj/" + this.resolveHash(0x7368841395eef392L);
    final String shopkeeper = "/sobj/" + this.resolveHash(0xfec05a2adaeea41cL);
    final String orangeDress = "/sobj/" + this.resolveHash(0xb595ecb662023356L);

    replaceMrgLines(
      root,
      transformations,
      "SECT/DRGN22.BIN/1172/mrg",
      new Int2ObjectOpenHashMap<>(Map.of(
        231, bridgePacer + "/model;436", // Bridge pacer
        232, bridgePacer + "/animations/" + this.resolveHash(0x8f705ecca0d34e98L) + ";52", // Standing still animation
        233, bridgePacer + "/animations/" + this.resolveHash(0xe50ce04cc0dd0c87L) + ";52", // Walking animation
        264, blueDress + "/model;436",
        265, blueDress + "/animations/" + this.resolveHash(0xffc5225ab65e1820L) + ";52", // Standing still animation
        297, shopkeeper + "/model;8944", // Shopkeeper
        298, shopkeeper + "/animations/" + this.resolveHash(0xedf1bd6c2c01682aL) + ";52", // Standing still animation
        330, orangeDress + "/model;436",
        331, orangeDress + "/animations/" + this.resolveHash(0xde4529dbb384ffb3L) + ";52", // Standing still animation
        336, orangeDress + "/animations/" + this.resolveHash(0xeb9ae41f4c219a46L) + ";52" // Interaction animation
      ))
    );

    replaceMrgLines(
      root,
      transformations,
      "SECT/DRGN22.BIN/1172/textures/mrg",
      new Int2ObjectArrayMap<>(Map.of(
        7, bridgePacer + "/textures/" + this.resolveHash(0xd09fed47c452f7abL) + ";4640",
        8, blueDress + "/textures/" + this.resolveHash(0x82e2cf59cecad171L) + ";4640",
        9, shopkeeper + "/textures/" + this.resolveHash(0x6526741a72c48321L) + ";4640",
        10, orangeDress + "/textures/" + this.resolveHash(0xa9117fe8067ad984L) + ";4640"
      ))
    );
  }

  private void fletzSendoff2Rouge(
    final PathNode root,
    final Transformations transformations,
    final Set<String> flags
  ) {
    final String knight = "/sobj/" + this.resolveHash(0x97bc21af6290806aL);

    replaceMrgLines(
      root,
      transformations,
      "SECT/DRGN22.BIN/785/mrg",
      new Int2ObjectOpenHashMap<>(Map.of(
        198, knight + "/model;436", // Knight
        199, knight + "/animations/" + this.resolveHash(0x6f2ce8ef2a2a8454L) + ";52" // Standing still animation
      ))
    );

    replaceMrgLines(
      root,
      transformations,
      "SECT/DRGN24.BIN/182/mrg",
      new Int2ObjectOpenHashMap<>(Map.of(
        198, knight + "/model;436", // Knight
        199, knight + "/animations/" + this.resolveHash(0x6f2ce8ef2a2a8454L) + ";52" // Standing still animation
      ))
    );

    replaceMrgLines(
      root,
      transformations,
      "SECT/DRGN22.BIN/785/textures/mrg",
      new Int2ObjectArrayMap<>(Map.of(
        6, knight + "/textures/" + this.resolveHash(0xfb91d2610cdcfb14L) + ";4640"
      ))
    );

    replaceMrgLines(
      root,
      transformations,
      "SECT/DRGN24.BIN/182/textures/mrg",
      new Int2ObjectArrayMap<>(Map.of(
        6, knight + "/textures/" + this.resolveHash(0xfb91d2610cdcfb14L) + ";4640"
      ))
    );
  }
}
