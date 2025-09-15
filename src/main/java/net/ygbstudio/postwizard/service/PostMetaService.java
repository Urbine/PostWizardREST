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
import java.util.List;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ygbstudio.postwizard.dao.PostMetaReaderDAO;
import net.ygbstudio.postwizard.dto.ClientPostMeta;
import net.ygbstudio.postwizard.models.Ethnicity;
import net.ygbstudio.postwizard.models.HairColor;
import net.ygbstudio.postwizard.models.Orientation;
import net.ygbstudio.postwizard.models.PostMetaKeys;
import net.ygbstudio.postwizard.models.Production;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

/**
 * Service class for managing post metadata in the postwizard application. This class provides
 * methods to validate, update, and retrieve post metadata based on the ClientPostMeta DTO and
 * various enumerations representing valid metadata values.
 *
 * <p>The service interacts with the PostMetaReaderDAO to perform database operations related to
 * post metadata.
 *
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
  @Inject private PostMetaReaderDAO dbPostMetaDao;

  /**
   * Checks if the post has any metadata fields.
   *
   * @param postID the ID of the post to check
   * @return true if the post has metadata fields, false otherwise
   */
  public boolean hasMetaFields(long postID) {
    return dbPostMetaDao.postExists(postID);
  }

  /**
   * Checks whether the provided hair color is valid based on the HairColor enumeration.
   *
   * @param hairColor
   * @return true if valid, false otherwise
   */
  public boolean isValidHairColor(@Nullable String hairColor) {
    return Objects.nonNull(hairColor)
        ? isInEnum(HairColor.class, String::valueOf, hairColor::equalsIgnoreCase)
        : false;
  }

  /**
   * Checks whether the provided ethnicity is valid based on the Ethnicity enumeration.
   *
   * @param ethnicity
   * @return true if valid, false otherwise
   */
  public boolean isValidEthnicity(@Nullable String ethnicity) {
    return Objects.nonNull(ethnicity)
        ? isInEnum(Ethnicity.class, String::valueOf, ethnicity::equalsIgnoreCase)
        : false;
  }

  /**
   * Checks whether the provided orientation is valid based on the Orientation enumeration.
   *
   * @param orientation
   * @return true if valid, false otherwise
   */
  public boolean isValidOrientation(@Nullable String orientation) {
    return Objects.nonNull(orientation)
        ? isInEnum(Orientation.class, String::valueOf, orientation::equalsIgnoreCase)
        : false;
  }

  /**
   * Checks whether the provided production is valid based on the Production enumeration.
   *
   * @param production
   * @return true if valid, false otherwise
   */
  public boolean isValidProduction(@Nullable String production) {
    return Objects.nonNull(production)
        ? isInEnum(Production.class, String::valueOf, production::equalsIgnoreCase)
        : false;
  }

  /**
   * Retrieves metadata for all posts and converts them into a List of ClientPostMeta objects. This
   * method fetches all post IDs from the database and maps each ID to its corresponding
   * ClientPostMeta object using the getClientPostMeta method.
   *
   * @return a List of ClientPostMeta objects representing metadata for all posts
   */
  public List<ClientPostMeta> getClientPostMetaAll() {
    return dbPostMetaDao.getPostIDs().parallelStream()
        .map(post -> getClientPostMeta(post))
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

    getTransformClassFields(clientPost.getClass(), Field::getName)
        .forEach(
            p -> {
              switch (enumFromValue(PostMetaKeys.class, p, true).orElse(PostMetaKeys.OTHERS)) {
                case ID:
                  break;
                case HOURS:
                  if (clientPost.getHours() != 0)
                    dbPostMetaDao.updatePostMetaAuto(
                        postId,
                        PostMetaKeys.HOURS.toString(),
                        Long.toString(clientPost.getHours()),
                        autoCreate);
                  break;
                case MINUTES:
                  if (clientPost.getMinutes() != 0)
                    dbPostMetaDao.updatePostMetaAuto(
                        postId,
                        PostMetaKeys.MINUTES.toString(),
                        Long.toString(clientPost.getMinutes()),
                        autoCreate);
                  break;
                case SECONDS:
                  if (clientPost.getSeconds() != 0)
                    dbPostMetaDao.updatePostMetaAuto(
                        postId,
                        PostMetaKeys.SECONDS.toString(),
                        Long.toString(clientPost.getSeconds()),
                        autoCreate);
                  break;
                case EMBED:
                  String embedCode = clientPost.getEmbedCode();
                  if (Objects.nonNull(embedCode) && !embedCode.isBlank())
                    dbPostMetaDao.updatePostMetaAuto(
                        postId, PostMetaKeys.EMBED.toString(), embedCode, autoCreate);
                  break;
                case PRODUCTION:
                  String clientProduction = clientPost.getVideoProduction();
                  if (isValidProduction(clientProduction))
                    dbPostMetaDao.updatePostMetaAuto(
                        postId, PostMetaKeys.PRODUCTION.toString(), clientProduction, autoCreate);
                  break;
                case ORIENTATION:
                  String clientOrientation = clientPost.getVideoOrientation();
                  if (isValidOrientation(clientOrientation))
                    dbPostMetaDao.updatePostMetaAuto(
                        postId, PostMetaKeys.ORIENTATION.toString(), clientOrientation, autoCreate);
                  break;
                case ETHNICITY:
                  String clientEthnicity = clientPost.getEthnicity();
                  if (isValidEthnicity(clientEthnicity))
                    dbPostMetaDao.updatePostMetaAuto(
                        postId,
                        PostMetaKeys.ETHNICITY.toString(),
                        StringUtils.capitalize(clientEthnicity),
                        autoCreate);
                  break;
                case HAIRCOLOR:
                  String clientHairColor = clientPost.getHairColor();
                  if (isValidHairColor(clientHairColor))
                    dbPostMetaDao.updatePostMetaAuto(
                        postId,
                        PostMetaKeys.HAIRCOLOR.toString(),
                        StringUtils.capitalize(clientHairColor),
                        autoCreate);
                  break;
                case HDVIDEO:
                  if (clientPost.getVideoHD() != null)
                    dbPostMetaDao.updatePostMetaAuto(
                        postId,
                        PostMetaKeys.HDVIDEO.toString(),
                        clientPost.getVideoHD() ? "on" : "off",
                        autoCreate);
                  break;
                case THUMBNAIL:
                  String thumbURI = clientPost.getThumbURI();
                  if (Objects.nonNull(thumbURI) && !thumbURI.isBlank())
                    dbPostMetaDao.updatePostMetaAuto(
                        postId, PostMetaKeys.THUMBNAIL.toString(), thumbURI, autoCreate);
                  break;
                case VIDEOURL:
                  String videoURL = clientPost.getVideoURL();
                  if (Objects.nonNull(videoURL) && !videoURL.isBlank())
                    dbPostMetaDao.updatePostMetaAuto(
                        postId, PostMetaKeys.VIDEOURL.toString(), videoURL, autoCreate);
                  break;
                case YOAST_FOCUSKW:
                  String focusKW = clientPost.getYoastFocusKW();
                  if (Objects.nonNull(focusKW) && !focusKW.isBlank())
                    dbPostMetaDao.updatePostMetaAuto(
                        postId, PostMetaKeys.YOAST_FOCUSKW.toString(), focusKW, autoCreate);
                  break;
                case YOAST_METADESC:
                  String metaDesc = clientPost.getYoastMetaDesc();
                  if (Objects.nonNull(metaDesc) && !metaDesc.isBlank())
                    dbPostMetaDao.updatePostMetaAuto(
                        postId, PostMetaKeys.YOAST_METADESC.toString(), metaDesc, autoCreate);
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

    dbPostMetaDao
        .getEntriesByPostID(postID)
        .forEach(
            p -> {
              String metaFieldKey = p.getMetaFieldKey();
              String metaFieldValue = p.getMetaFieldValue();

              if (convertedObj.getID() == 0) convertedObj.setID(p.getPostID());

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
                      Objects.requireNonNullElse(metaFieldValue, "").equals("on"));
                  break;
                case THUMBNAIL:
                  convertedObj.setThumb(metaFieldValue);
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
