package legend.game.unpacker.sobj;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import legend.game.unpacker.PathNode;
import legend.game.unpacker.Transformations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.xxhash.XXH3State;
import org.lwjgl.util.xxhash.XXHash;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class SobjTransformer {
  private SobjTransformer() {}

  private static final Logger LOGGER = LogManager.getFormatterLogger(SobjTransformer.class);
  private static final int MAX_SIZE = 84580;

  public static void transform(final PathNode root, final Transformations transformations, final Set<String> flags) {
    final XXH3State xxhash = XXHash.XXH3_createState();
    if(xxhash == null) {
      LOGGER.error("Failed to initialize hash state. Skipping.");
      return;
    }

    final Long2ObjectMap<SobjModel> models = new Long2ObjectArrayMap<>();
    final Long2ObjectMap<String> namer = initNamer();
    final Map<Long, ReplacementModel> replacer = initReplacer();

    final ByteBuffer buffer = BufferUtils.createByteBuffer(MAX_SIZE);

    final PathNode sect = root.children.get("SECT");

    if(sect == null) {
      return;
    }

    for(int drgnIndex = 1; drgnIndex <= 4; drgnIndex++) {
      final PathNode drgn = sect.children.get("DRGN2" + drgnIndex + ".BIN");

      if(drgn == null) {
        continue;
      }

      final int drgnEntries = countMrg(drgn);

      for(int map = 5; map < drgnEntries; map +=3) {
        final PathNode mainFolder = drgn.children.get(Integer.toString(map));

        if(mainFolder == null) {
          continue;
        }

        final PathNode textures = mainFolder.children.get("textures");

        if(textures == null) {
          continue;
        }

        final PathNode mrg = mainFolder.children.get("mrg");
        final RefArchive refArchive = new RefArchive(mrg);

        final PathNode textureMrg = textures.children.get("mrg");
        final RefArchive textureRefArchive = new RefArchive(textureMrg);

        final int[] modelMap = refArchive.parseModels();
        final long[] modelHashes = new long[modelMap.length];

        for(int modelIndex = 0; modelIndex < modelMap.length; modelIndex++) {
          final RefArchive.Ref ref = refArchive.files.get(modelIndex * 33);

          if(ref.virtual) {
            continue;
          }

          final PathNode model = mainFolder.children.get(Integer.toString(modelIndex * 33));

          XXHash.XXH3_64bits_reset(xxhash);

          buffer.clear();
          buffer.put(model.data.getBytes());
          buffer.flip();

          XXHash.XXH3_64bits_update(xxhash, buffer);

          final long modelHash = XXHash.XXH3_64bits_digest(xxhash);

          if(!models.containsKey(modelHash)) {
            models.put(modelHash, new SobjModel(model.data));
          }

          modelHashes[modelIndex] = modelHash;

          ref.ref = resolveModelReference(modelHash, replacer, namer);
          transformations.removeNode(model);
        }

        for(int modelIndex = 0; modelIndex < modelMap.length; modelIndex++) {
          final long modelHash = modelHashes[modelMap[modelIndex]];
          final SobjModel model = models.get(modelHash);

          final RefArchive.Ref textureRef = textureRefArchive.files.get(modelIndex);

          // DRGN23/173/16 texture is missing, It would be nice to have a better solution
          if(textureRef != null && !textureRef.virtual) {
            final PathNode texture = textures.children.get(Integer.toString(modelIndex));

            XXHash.XXH3_64bits_reset(xxhash);

            buffer.clear();
            buffer.put(texture.data.getBytes());
            buffer.flip();

            XXHash.XXH3_64bits_update(xxhash, buffer);

            final long textureHash = XXHash.XXH3_64bits_digest(xxhash);
            if(!model.textures.containsKey(textureHash)) {
              model.textures.put(textureHash, texture.data);
            }

            textureRef.ref = resolveTextureReference(modelHash, textureHash, replacer, namer);
            transformations.removeNode(texture);
          }

          final int baseIndex = modelIndex * 33;
          for(int animationIndex = 1; animationIndex <= 32; animationIndex++) {
            final RefArchive.Ref animationRef = refArchive.files.get(baseIndex + animationIndex);

            if(animationRef.virtual) {
              continue;
            }

            final PathNode animation = mainFolder.children.get(Integer.toString(baseIndex + animationIndex));

            XXHash.XXH3_64bits_reset(xxhash);

            buffer.clear();
            buffer.put(animation.data.getBytes());
            buffer.flip();

            XXHash.XXH3_64bits_update(xxhash, buffer);

            final long animationHash = XXHash.XXH3_64bits_digest(xxhash);
            if(!model.animations.containsKey(animationHash)) {
              model.animations.put(animationHash, animation.data);
            }

            animationRef.ref = resolveAnimationReference(modelHash, animationHash, replacer, namer);
            transformations.removeNode(animation);
          }
        }

        transformations.replaceNode(mrg, refArchive.toFileData());
        transformations.replaceNode(textureMrg, textureRefArchive.toFileData());
      }
    }

    XXHash.XXH3_64bits_reset(xxhash);

    final PathNode sobjFolder = new PathNode("sobj", "sobj", null, root);
    transformations.addNode(sobjFolder);

    for(final var entry : models.long2ObjectEntrySet()) {
      final long key = entry.getLongKey();
      final String name = namer.getOrDefault(key, String.format("%016x", key));
      final PathNode folder = transformations.addChild(sobjFolder, name, null);

      transformations.addChild(folder, "model", entry.getValue().file);

      final PathNode textures = transformations.addChild(folder, "textures", null);

      for(final var textureEntry : entry.getValue().textures.long2ObjectEntrySet()) {
        final long textureKey = textureEntry.getLongKey();
        final String textureName = namer.getOrDefault(textureKey, String.format("%016x", textureKey));
        transformations.addChild(textures, textureName, textureEntry.getValue());
      }

      final PathNode animations = transformations.addChild(folder, "animations", null);

      for(final var animationEntry : entry.getValue().animations.long2ObjectEntrySet()) {
        final long animationKey = animationEntry.getLongKey();
        final String animationName = namer.getOrDefault(animationKey, String.format("%016x", animationKey));
        transformations.addChild(animations, animationName, animationEntry.getValue());
      }
    }
  }

  private static int countMrg(final PathNode parent) {
    final PathNode mrg = parent.children.get("mrg");

    if(mrg == null) {
      return 0;
    }

    final String mrgContents = mrg.data.readFixedLengthAscii(0x0, mrg.data.size());

    return (int)mrgContents.lines().count();
  }

  public static Long2ObjectMap<String> initNamer() {
    final Long2ObjectMap<String> namer = new Long2ObjectOpenHashMap<>();

    try(final BufferedReader reader = Files.newBufferedReader(Path.of(".", "sobj_namer.txt"))) {
      reader.lines().forEach(line -> {
        line = line.trim();

        if(line.isEmpty() || line.startsWith("#")) {
          return;
        }

        // Remove trailing comments
        final int commentIndex = line.indexOf('#');
        if (commentIndex >= 0) {
          line = line.substring(0, commentIndex).trim();
        }

        final String[] parts = line.split("=");

        if(parts.length != 2) {
          return;
        }

        final long hash = Long.parseUnsignedLong(parts[0].trim(), 16);
        final String name = parts[1].trim();

        namer.put(hash, name);
      });
    } catch(final IOException e) {
      LOGGER.error("Failed to load names file. Skipping naming.", e);
    }

    return namer;
  }

  private static Map<Long, ReplacementModel> initReplacer() {
    try {
      final SimpleModule module = new SimpleModule();

      module.addKeyDeserializer(Long.class, new HexLongKeyDeserializer());

      final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      mapper.registerModule(module);

      final ReplacementConfig config = mapper.readValue(Path.of(".", "sobj_replacer.yaml").toFile(), ReplacementConfig.class);

      return config.replacer;
    } catch(final IOException e) {
      LOGGER.error("Failed to initialize model replacer. Skipping... ", e);
    }

    return new HashMap<>();
  }

  private static String resolveModelReference(final long hash, final Map<Long, ReplacementModel> replacer, final Long2ObjectMap<String> namer) {
    final ReplacementModel model = replacer.get(hash);

    final long resolvedHash = model != null
      ? model.replacement
      : hash;

    final String name = namer.getOrDefault(resolvedHash, String.format("%016x", resolvedHash));

    return "/sobj/" + name + "/model";
  }

  private static String resolveTextureReference(final long modelHash, final long textureHash, final Map<Long, ReplacementModel> replacer, final Long2ObjectMap<String> namer) {
    final ReplacementModel replacementModel = replacer.get(modelHash);

    final ReplacementAsset replacementTexture = replacementModel != null
      ? replacementModel.textures.get(textureHash)
      : null;

    final long resolvedModelHash = replacementTexture != null
      ? replacementTexture.modelHash
      : modelHash;

    final String modelName = namer.getOrDefault(resolvedModelHash, String.format("%016x", resolvedModelHash));

    final long resolvedTextureHash = replacementTexture != null
      ? replacementTexture.assetHash
      : textureHash;

    final String textureName = namer.getOrDefault(resolvedTextureHash, String.format("%016x", resolvedTextureHash));

    return "/sobj/" + modelName + "/textures/" + textureName;
  }

  private static String resolveAnimationReference(final long modelHash, final long animationHash, final Map<Long, ReplacementModel> replacer, final Long2ObjectMap<String> namer) {
    final ReplacementModel replacementModel = replacer.get(modelHash);

    final ReplacementAsset replacementAnimation = replacementModel != null
      ? replacementModel.animations.get(animationHash)
      : null;

    final long resolvedModelHash = replacementAnimation != null
      ? replacementAnimation.modelHash
      : modelHash;

    final String modelName = namer.getOrDefault(resolvedModelHash, String.format("%016x", resolvedModelHash));

    final long resolvedAnimationHash = replacementAnimation != null
      ? replacementAnimation.assetHash
      : animationHash;

    final String animationName = namer.getOrDefault(resolvedAnimationHash, String.format("%016x", resolvedAnimationHash));

    return "/sobj/" + modelName + "/animations/" + animationName;
  }
}
