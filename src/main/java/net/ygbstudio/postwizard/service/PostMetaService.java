package net.ygbstudio.postwizard.service;

import static net.ygbstudio.postwizard.utils.Helpers.enumFromValue;
import static net.ygbstudio.postwizard.utils.Helpers.isInEnum;
import static net.ygbstudio.postwizard.utils.Logging.logStepIn;
import static net.ygbstudio.postwizard.utils.Logging.logStepOut;
import static net.ygbstudio.postwizard.utils.Logging.loggingInit;
import static net.ygbstudio.postwizard.utils.Reflection.getTransformClassFields;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.ygbstudio.postwizard.dao.PostMetaManager;
import net.ygbstudio.postwizard.dto.ClientPost;
import net.ygbstudio.postwizard.dto.ClientPostMeta;
import net.ygbstudio.postwizard.entities.WPMeta;
import net.ygbstudio.postwizard.models.Ethnicity;
import net.ygbstudio.postwizard.models.HairColor;
import net.ygbstudio.postwizard.models.Orientation;
import net.ygbstudio.postwizard.models.PostMetaKeys;
import net.ygbstudio.postwizard.models.Production;
import net.ygbstudio.postwizard.models.ToggleField;
import net.ygbstudio.postwizard.rest.PostController;
import net.ygbstudio.postwizard.utils.function.QuadConsumer;
import net.ygbstudio.postwizard.utils.function.QuadFunction;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Service class for managing post metadata in the PostWizard application. This class provides
 * methods to validate, update, and retrieve post metadata based on the ClientPostMeta DTO and
 * various enumerations representing valid metadata values.
 *
 * <p>The service interacts with the PostMetaManager to perform database operations related to post
 * metadata. PostMetaService also defines additional transactional boundaries for some methods to
 * ensure data consistency and integrity by isolating the transactional context of the database
 * operations with every method call.
 *
 * @see PostMetaManager
 * @see PostController
 * @author Yoham Gabriel @ YGB Studio
 */
@ApplicationScoped
public class PostMetaService {

  private static final Logger postMetaServiceLog =
      Logger.getLogger(PostMetaService.class.getName());

  @SuppressWarnings("unused")
  private static final FileHandler logFileHandler =
      loggingInit(postMetaServiceLog, Level.ALL, true);

  /** Data Access Object (DAO) for reading and manipulating the post metadata table. */
  @Inject private PostMetaManager dbPostMetaManager;

  /**
   * Checks if the post has any metadata fields.
   *
   * @param postID the ID of the post to check
   * @return true if the post has metadata fields, false otherwise
   */
  @Transactional(value = TxType.REQUIRES_NEW)
  public boolean hasMetaFields(long postID) {
    return dbPostMetaManager.postExists(postID);
  }

  /**
   * Checks whether the provided hair color is valid based on the HairColor enumeration.
   *
   * @param hairColor
   * @return true if valid, false otherwise
   */
  public boolean isValidHairColor(@Nullable String hairColor) {
    return Objects.nonNull(hairColor)
        && isInEnum(HairColor.class, String::valueOf, hairColor::equalsIgnoreCase);
  }

  /**
   * Checks whether the provided ethnicity is valid based on the Ethnicity enumeration.
   *
   * @param ethnicity
   * @return true if valid, false otherwise
   */
  public boolean isValidEthnicity(@Nullable String ethnicity) {
    return Objects.nonNull(ethnicity)
        && isInEnum(Ethnicity.class, String::valueOf, ethnicity::equalsIgnoreCase);
  }

  /**
   * Checks whether the provided orientation is valid based on the Orientation enumeration.
   *
   * @param orientation
   * @return true if valid, false otherwise
   */
  public boolean isValidOrientation(@Nullable String orientation) {
    return Objects.nonNull(orientation)
        && isInEnum(Orientation.class, String::valueOf, orientation::equalsIgnoreCase);
  }

  /**
   * Checks whether the provided toggle field is valid based on the ToggleField enumeration.
   *
   * @param toggleField
   * @return true if valid, false otherwise
   */
  public boolean isValidToggleField(@Nullable String toggleField) {
    return Objects.nonNull(toggleField)
        && isInEnum(ToggleField.class, String::valueOf, toggleField::equalsIgnoreCase);
  }

  /**
   * Checks whether the provided production is valid based on the Production enumeration.
   *
   * @param production
   * @return true if valid, false otherwise
   */
  public boolean isValidProduction(@Nullable String production) {
    return Objects.nonNull(production)
        && isInEnum(Production.class, String::valueOf, production::equalsIgnoreCase);
  }

  /**
   * Filters entries by meta key, filters them by a given predicate and transforms them using a
   * given function applicable to the WPMeta object. The function returns a new set of the
   * transformed objects.
   *
   * @param metaKey the meta key to filter the metadata entries by
   * @param filterPredicate the predicate to apply to each WPMeta object
   * @param transformer the function to apply to each WPMeta object
   * @return Set of Long containing the IDs of featured videos.
   */
  @Transactional(value = TxType.REQUIRES_NEW)
  public <R> Set<R> filterMetaKeyEntriesBy(
      PostMetaKeys metaKey,
      Predicate<? super WPMeta> filterPredicate,
      Function<? super WPMeta, R> transformer) {

    return dbPostMetaManager
        .getEntriesByMetaKey(metaKey.toString())
        .filter(filterPredicate)
        .map(transformer)
        .collect(Collectors.toUnmodifiableSet());
  }

  /**
   * Retrieves the value of a specific metadata key for a given post ID.
   *
   * @param postID the ID of the post
   * @param metaKey the PostMetaKeys enum member representing the metadata key to retrieve
   * @return an Optional containing the metadata value if found, or empty if not found
   */
  @Transactional(value = TxType.REQUIRES_NEW)
  public Optional<String> getMetaValueByPostID(long postID, PostMetaKeys metaKey) {
    return dbPostMetaManager
        .findMetaKeyByPostID(metaKey.toString(), postID)
        .map(WPMeta::getMetaFieldValue);
  }

  /**
   * Retrieves the value of a specific metadata key for a given post ID.
   *
   * @param metaValuePattern the pattern to match the metadata value (e.g. "%.jpg%")
   * @param metaKey the PostMetaKeys enum member representing the metadata key to retrieve
   * @return an Optional containing the metadata value if found, or empty if not found
   */
  @Transactional(value = TxType.REQUIRES_NEW)
  public Optional<String> getMetaValueLike(
      @NonNull String metaValuePattern, @NonNull PostMetaKeys metaKey) {
    return dbPostMetaManager.findMetaValueLike(metaValuePattern, metaKey.toString());
  }

  /**
   * Auto thumbnail matching method.
   *
   * <p>This method attempts to find a matching media file for the given post ID and attachment post
   * slug if, and only if, {@code attachmentPost} is present and its name matches the slug pattern
   * {@code %%<slug>%%} from {@code postID}. If no matching media file in {@code wp_postmeta} is
   * found, the GUID of {@code attachmentPost} is fetched and used as a fallback.
   *
   * @param postId the target post to update with the thumbnail path
   * @param siteUploadsPath the uploads URL prefix with the site URL. Typically, this is provided by
   *     the {@link EnvironmentService}.
   * @param attachmentPost the media post that matches the slug pattern of the target post.
   * @param mediaPostSupplier a supplier that returns (lazily) the media post that contains the GUID
   *     if no matching media file is found in {@code wp_postmeta}.
   * @return true if the thumbnail was updated successfully, false otherwise (in case of exception).
   */
  @Transactional(value = TxType.REQUIRES_NEW)
  public boolean autoThumbMatch(
      long postId,
      @Nullable String siteUploadsPath,
      @NonNull ClientPost attachmentPost,
      Supplier<ClientPost> mediaPostSupplier) {
    Optional<String> mediaFile =
        getMetaValueLike(
            String.format("%%%s%%", attachmentPost.getSlug()), PostMetaKeys.WP_ATTACHED_FILE);

    String guid = null;
    if (mediaFile.isEmpty() || mediaFile.get().isBlank()) {
      guid = mediaPostSupplier.get().getGuid();

      // If there is no thumbnail match in `wp_postmeta` or `wp_posts`, return false
      if (guid == null) return false;
    }

    QuadFunction<Long, PostMetaKeys, String, Boolean, Boolean> updatePostMetaAuto =
        (postID, metaKey, metaValue, autoCreate) -> {
          if (postID <= 0) return false;
          try {
            dbPostMetaManager.updatePostMetaAuto(postID, metaKey.toString(), metaValue, autoCreate);
            return true;
          } catch (Exception anyEx) {
            postMetaServiceLog.warning(
                () -> "Failed to update post meta auto: " + anyEx.getMessage());
            postMetaServiceLog.fine(
                () ->
                    "Failed to update post meta auto. Stacktrace: "
                        + Arrays.toString(anyEx.getStackTrace()));
            return false;
          }
        };

    if (mediaFile.isPresent() && !mediaFile.get().isBlank()) {
      String thumbUploadPath =
          Objects.nonNull(siteUploadsPath)
              ? String.format("%s/%s", siteUploadsPath, mediaFile.get())
              : "";
      return updatePostMetaAuto.apply(postId, PostMetaKeys.THUMBNAIL, thumbUploadPath, true);
    } else if (guid != null && !guid.isBlank()) {
      return updatePostMetaAuto.apply(postId, PostMetaKeys.THUMBNAIL, guid, true);
    }

    return false;
  }

  /*
   * Get random post IDs from the database method.
   *
   * @param metaKey the PostMetaKeys enum member to filter the metadata entries by
   * @param limitBy the number of random post IDs to retrieve.
   * @param filterPredicate the predicate to apply to each WPMeta object
   * @return List of Long containing the IDs of random posts.
   */
  @Transactional(value = TxType.REQUIRES_NEW)
  public Set<WPMeta> getRandomPostsByMetaKey(
      PostMetaKeys metaKey, long limitBy, Predicate<? super WPMeta> filterPredicate) {
    return dbPostMetaManager.getRandomPostsByMetaKey(metaKey.toString(), limitBy, filterPredicate);
  }

  /*
   * Modifies the featured flag for a list of post IDs based on the toggle field provided.
   *
   * @param setOfPostIDs the set of post IDs to modify (toggle)
   * @param toggle the ToggleField enum member that defines the operation to perform.
   */
  public Set<Long> toggleFeaturedVideos(Set<Long> setOfPostIDs, ToggleField toggle) {
    setOfPostIDs.forEach(
        postID ->
            dbPostMetaManager.updatePostMetaAuto(
                postID, PostMetaKeys.FEATURED.toString(), toggle.toString(), false));

    return setOfPostIDs;
  }

  /*
   * Removes the featured flag for all videos in the database.
   *
   * @return List of Long containing the IDs videos with the featured flag removed.
   */
  public Set<Long> disableFeaturedVideos() {
    Set<Long> getAllPostIDs =
        filterMetaKeyEntriesBy(
            PostMetaKeys.FEATURED,
            post -> post.getMetaFieldValue().equals(ToggleField.ON.toString()),
            post -> post.getPost().getId());
    return toggleFeaturedVideos(getAllPostIDs, ToggleField.OFF);
  }

  /*
   * Enables the featured flag for all videos in the database.
   *
   * @param setOfPostIDs the set of post IDs to modify (feature)
   * @return List of Long containing the IDs videos with the featured flag enabled.
   */
  public Set<Long> featureVideos(Set<Long> setOfPostIDs) {
    return toggleFeaturedVideos(setOfPostIDs, ToggleField.ON);
  }

  /**
   * Randomises the featured videos in the database.
   *
   * @param newFeaturedVids the number of videos to feature in this randomisation batch.
   * @return Set of Long containing the IDs videos with the featured flag enabled.
   */
  public Set<Long> randomiseFeaturedVideos(int newFeaturedVids) {
    if (newFeaturedVids < 0)
      throw new IllegalArgumentException("newFeaturedVids must be greater than 0");
    if (newFeaturedVids == 0) return Collections.emptySet();

    Set<Long> oldFeatured = disableFeaturedVideos();

    Predicate<? super WPMeta> excludePredicate =
        oldFeatured.isEmpty()
            ? post ->
                post.getMetaFieldKey().equals(PostMetaKeys.FEATURED.toString())
                    && post.getMetaFieldValue().equals(ToggleField.OFF.toString())
            : post -> !oldFeatured.contains(post.getPost().getId());

    Set<Long> randomPostIDs =
        getRandomPostsByMetaKey(PostMetaKeys.FEATURED, newFeaturedVids, excludePredicate).stream()
            .map(post -> post.getPost().getId())
            .collect(Collectors.toUnmodifiableSet());

    if (randomPostIDs.size() != newFeaturedVids)
      throw new UnsupportedOperationException("Got more or less IDs than expected");

    return featureVideos(randomPostIDs);
  }

  /**
   * Retrieves metadata for all posts and converts them into a List of ClientPostMeta objects. This
   * method fetches all post IDs from the database and maps each ID to its corresponding
   * ClientPostMeta object using the getClientPostMeta method.
   *
   * @return a List of ClientPostMeta objects representing metadata for all posts
   */
  @Transactional(value = TxType.REQUIRES_NEW)
  public List<ClientPostMeta> getClientPostMetaAll() {
    return dbPostMetaManager.getPostIDs().parallelStream()
        .map(this::getClientPostMeta)
        .filter(post -> Objects.nonNull(post.getVideoURL()) || Objects.nonNull(post.getEmbedCode()))
        .toList();
  }

  /**
   * Updates the post metadata based on the provided ClientPostMeta object. This method iterates
   * through the properties of the ClientPostMeta object and updates the corresponding metadata in
   * the database. Unlike posts, metadata fields can be created if the schema provided by the client
   * is correct and constitutes a relevant key in the WordPress site.
   *
   * <p>This method runs within a new transaction to ensure that the updates are isolated from other
   * operations and can be committed or rolled back independently.
   *
   * @param clientPost the ClientPostMeta object containing post metadata to update
   * @param autoCreate boolean indicating whether to create metadata if it does not exist
   */
  @Transactional(value = TxType.REQUIRES_NEW)
  public void clientPostMetaUpdateStrategy(ClientPostMeta clientPost, boolean autoCreate) {
    logStepIn(postMetaServiceLog, clientPost);

    long postId = clientPost.getID();
    if (postId <= 0) return;

    QuadConsumer<Long, PostMetaKeys, String, Boolean> updatePostMetaAuto =
        (postID, metaKey, metaValue, create) -> {
          try {
            dbPostMetaManager.updatePostMetaAuto(postID, metaKey.toString(), metaValue, create);
          } catch (Exception anyEx) {
            postMetaServiceLog.warning(
                () ->
                    "Failed to update post meta auto -> Key: "
                        + metaKey
                        + ": "
                        + anyEx.getMessage());
            postMetaServiceLog.fine(
                () ->
                    "Failed to update post meta auto in clientPostMetaUpdateStrategy -> "
                        + metaKey
                        + ". Stacktrace: "
                        + Arrays.toString(anyEx.getStackTrace()));
          }
        };

    getTransformClassFields(clientPost.getClass(), Field::getName)
        .forEach(
            p -> {
              switch (enumFromValue(PostMetaKeys.class, p, true).orElse(PostMetaKeys.OTHERS)) {
                case HOURS:
                  if (clientPost.getHours() != 0)
                    updatePostMetaAuto.accept(
                        postId,
                        PostMetaKeys.HOURS,
                        Long.toString(clientPost.getHours()),
                        autoCreate);
                  break;
                case MINUTES:
                  if (clientPost.getMinutes() != 0)
                    updatePostMetaAuto.accept(
                        postId,
                        PostMetaKeys.MINUTES,
                        Long.toString(clientPost.getMinutes()),
                        autoCreate);
                  break;
                case SECONDS:
                  if (clientPost.getSeconds() != 0)
                    updatePostMetaAuto.accept(
                        postId,
                        PostMetaKeys.SECONDS,
                        Long.toString(clientPost.getSeconds()),
                        autoCreate);
                  break;
                case EMBED:
                  String embedCode = clientPost.getEmbedCode();
                  if (Objects.nonNull(embedCode) && !embedCode.isBlank())
                    updatePostMetaAuto.accept(postId, PostMetaKeys.EMBED, embedCode, autoCreate);
                  break;
                case PRODUCTION:
                  String clientProduction = clientPost.getVideoProduction();
                  if (isValidProduction(clientProduction))
                    updatePostMetaAuto.accept(
                        postId, PostMetaKeys.PRODUCTION, clientProduction, autoCreate);
                  break;
                case ORIENTATION:
                  String clientOrientation = clientPost.getVideoOrientation();
                  if (isValidOrientation(clientOrientation))
                    updatePostMetaAuto.accept(
                        postId, PostMetaKeys.ORIENTATION, clientOrientation, autoCreate);
                  break;
                case ETHNICITY:
                  String clientEthnicity = clientPost.getEthnicity();
                  if (isValidEthnicity(clientEthnicity))
                    updatePostMetaAuto.accept(
                        postId,
                        PostMetaKeys.ETHNICITY,
                        StringUtils.capitalize(clientEthnicity),
                        autoCreate);
                  break;
                case HAIRCOLOR:
                  String clientHairColor = clientPost.getHairColor();
                  if (isValidHairColor(clientHairColor))
                    updatePostMetaAuto.accept(
                        postId,
                        PostMetaKeys.HAIRCOLOR,
                        StringUtils.capitalize(clientHairColor),
                        autoCreate);
                  break;
                case HDVIDEO:
                  if (clientPost.getVideoHD() != null)
                    updatePostMetaAuto.accept(
                        postId,
                        PostMetaKeys.HDVIDEO,
                        clientPost.getVideoHD()
                            ? ToggleField.ON.toString()
                            : ToggleField.OFF.toString(),
                        autoCreate);
                  break;
                case THUMBNAIL:
                  String thumbURI = clientPost.getThumbURI();
                  if (Objects.nonNull(thumbURI) && !thumbURI.isBlank())
                    updatePostMetaAuto.accept(postId, PostMetaKeys.THUMBNAIL, thumbURI, autoCreate);
                  break;
                case VIDEOURL:
                  String videoURL = clientPost.getVideoURL();
                  if (Objects.nonNull(videoURL) && !videoURL.isBlank())
                    updatePostMetaAuto.accept(postId, PostMetaKeys.VIDEOURL, videoURL, autoCreate);
                  break;
                case YOAST_FOCUSKW:
                  String focusKW = clientPost.getYoastFocusKW();
                  if (Objects.nonNull(focusKW) && !focusKW.isBlank())
                    updatePostMetaAuto.accept(
                        postId, PostMetaKeys.YOAST_FOCUSKW, focusKW, autoCreate);
                  break;
                case YOAST_METADESC:
                  String metaDesc = clientPost.getYoastMetaDesc();
                  if (Objects.nonNull(metaDesc) && !metaDesc.isBlank())
                    updatePostMetaAuto.accept(
                        postId, PostMetaKeys.YOAST_METADESC, metaDesc, autoCreate);
                  break;
                default:
                  break;
              }
            });
  }

  /**
   * Retrieves the post metadata for a given post ID and converts it into a ClientPostMeta object.
   * This method fetches the metadata entries from the database and populates the ClientPostMeta
   * object with the corresponding values based on the PostMetaKeys enumeration.
   *
   * <p>This method is used to convert the raw metadata entries into a structured ClientPostMeta
   * object that can be easily consumed by the client without exposing the underlying database
   * structure and runs within a new transaction to ensure data consistency and isolation from other
   * operations.
   *
   * @param postID the ID of the post for which metadata is requested
   * @return ClientPostMeta object containing the post metadata
   */
  @Transactional(value = TxType.REQUIRES_NEW)
  public ClientPostMeta getClientPostMeta(long postID) {
    logStepIn(postMetaServiceLog, postID);
    ClientPostMeta convertedObj = new ClientPostMeta();

    dbPostMetaManager
        .getEntriesByPostID(postID)
        .forEach(
            p -> {
              String metaFieldKey = p.getMetaFieldKey();
              String metaFieldValue = p.getMetaFieldValue();

              if (convertedObj.getID() == 0) convertedObj.setID(p.getPost().getId());

              switch (enumFromValue(PostMetaKeys.class, metaFieldKey, true)
                  .orElse(PostMetaKeys.OTHERS)) {
                case HOURS:
                  if (metaFieldValue != null && metaFieldValue.matches("\\d+"))
                    convertedObj.setHours(Integer.parseInt(metaFieldValue));
                  break;
                case MINUTES:
                  if (metaFieldValue != null && metaFieldValue.matches("\\d+"))
                    convertedObj.setMinutes(Integer.parseInt(metaFieldValue));
                  break;
                case SECONDS:
                  if (metaFieldValue != null && metaFieldValue.matches("\\d+"))
                    convertedObj.setSeconds(Integer.parseInt(metaFieldValue));
                  break;
                case EMBED:
                  convertedObj.setEmbedCode(metaFieldValue);
                  break;
                case PRODUCTION:
                  convertedObj.setVideoProduction(metaFieldValue);
                  break;
                case ORIENTATION:
                  convertedObj.setVideoOrientation(metaFieldValue);
                  break;
                case ETHNICITY:
                  convertedObj.setEthnicity(metaFieldValue);
                  break;
                case HAIRCOLOR:
                  convertedObj.setHairColor(metaFieldValue);
                  break;
                case HDVIDEO:
                  convertedObj.setVideoHD(
                      Objects.requireNonNullElse(metaFieldValue, "")
                          .equals(ToggleField.ON.toString()));
                  break;
                case THUMBNAIL:
                  convertedObj.setThumbURI(metaFieldValue);
                  break;
                case VIDEOURL:
                  convertedObj.setVideoURL(metaFieldValue);
                  break;
                case YOAST_FOCUSKW:
                  convertedObj.setYoastFocusKW(metaFieldValue);
                  break;
                case YOAST_METADESC:
                  convertedObj.setYoastMetaDesc(metaFieldValue);
                  break;
                default:
                  break;
              }
            });

    logStepOut(postMetaServiceLog, convertedObj);
    return convertedObj;
  }
}
