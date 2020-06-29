package ealvatag.tag.id3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_ACCOMPANIMENT;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_ALBUM;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_ALBUM_ARTIST_SORT_ORDER_ITUNES;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_ALBUM_SORT_ORDER;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_ARTIST;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_ARTIST_SORT_ORDER;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_ATTACHED_PICTURE;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_AUDIO_ENCRYPTION;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_AUDIO_SEEK_POINT_INDEX;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_BPM;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_COMMENT;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_COMMERCIAL_FRAME;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_COMPOSER;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_COMPOSER_SORT_ORDER_ITUNES;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_CONDUCTOR;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_CONTENT_GROUP_DESC;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_COPYRIGHTINFO;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_ENCODEDBY;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_ENCODING_TIME;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_ENCRYPTION;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_EQUALISATION2;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_EVENT_TIMING_CODES;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_FILE_OWNER;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_FILE_TYPE;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_GENERAL_ENCAPS_OBJECT;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_GENRE;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_GROUP_ID_REG;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_HW_SW_SETTINGS;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_INITIAL_KEY;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_INVOLVED_PEOPLE;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_ISRC;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_IS_COMPILATION;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_LANGUAGE;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_LENGTH;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_LINKED_INFO;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_LYRICIST;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_MEDIA_TYPE;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_MOOD;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_MPEG_LOCATION_LOOKUP_TABLE;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_MUSICIAN_CREDITS;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_MUSIC_CD_ID;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_ORIGARTIST;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_ORIGINAL_RELEASE_TIME;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_ORIG_FILENAME;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_ORIG_LYRICIST;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_ORIG_TITLE;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_OWNERSHIP;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_PLAYLIST_DELAY;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_PLAY_COUNTER;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_POPULARIMETER;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_POSITION_SYNC;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_PRIVATE;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_PRODUCED_NOTICE;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_PUBLISHER;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_RADIO_NAME;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_RADIO_OWNER;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_RECOMMENDED_BUFFER_SIZE;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_RELATIVE_VOLUME_ADJUSTMENT2;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_RELEASE_TIME;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_REMIXED;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_REVERB;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_SEEK;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_SET;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_SET_SUBTITLE;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_SIGNATURE;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_SYNC_LYRIC;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_SYNC_TEMPO;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_TAGGING_TIME;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_TERMS_OF_USE;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_TITLE;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_TITLE_REFINEMENT;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_TITLE_SORT_ORDER;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_TRACK;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_UNIQUE_FILE_ID;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_UNSYNC_LYRICS;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_URL_ARTIST_WEB;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_URL_COMMERCIAL;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_URL_COPYRIGHT;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_URL_FILE_WEB;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_URL_OFFICIAL_RADIO;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_URL_PAYMENT;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_URL_PUBLISHERS;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_USER_DEFINED_INFO;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_USER_DEFINED_URL;
import static ealvatag.tag.id3.ID3v24Frames.FRAME_ID_YEAR;

/**
 * Orders frame Ids so that the most important frames are written first
 */
public class ID3v24PreferredFrameOrderComparator implements Comparator<String> {
    private static final List<String> frameIdsInPreferredOrder = new ArrayList<>();
    private static ID3v24PreferredFrameOrderComparator comparator;

    static {
        //these are the key ones we want at the top
        frameIdsInPreferredOrder.add(FRAME_ID_UNIQUE_FILE_ID);
        frameIdsInPreferredOrder.add(FRAME_ID_TITLE);
        frameIdsInPreferredOrder.add(FRAME_ID_ARTIST);
        frameIdsInPreferredOrder.add(FRAME_ID_ALBUM);
        frameIdsInPreferredOrder.add(FRAME_ID_ALBUM_SORT_ORDER);
        frameIdsInPreferredOrder.add(FRAME_ID_GENRE);
        frameIdsInPreferredOrder.add(FRAME_ID_COMPOSER);
        frameIdsInPreferredOrder.add(FRAME_ID_CONDUCTOR);
        frameIdsInPreferredOrder.add(FRAME_ID_CONTENT_GROUP_DESC);
        frameIdsInPreferredOrder.add(FRAME_ID_TRACK);
        frameIdsInPreferredOrder.add(FRAME_ID_YEAR);
        frameIdsInPreferredOrder.add(FRAME_ID_ACCOMPANIMENT);
        frameIdsInPreferredOrder.add(FRAME_ID_BPM);
        frameIdsInPreferredOrder.add(FRAME_ID_ISRC);
        frameIdsInPreferredOrder.add(FRAME_ID_TITLE_SORT_ORDER);
        frameIdsInPreferredOrder.add(FRAME_ID_TITLE_REFINEMENT);
        frameIdsInPreferredOrder.add(FRAME_ID_UNSYNC_LYRICS);
        frameIdsInPreferredOrder.add(FRAME_ID_USER_DEFINED_INFO);
        frameIdsInPreferredOrder.add(FRAME_ID_USER_DEFINED_URL);
        frameIdsInPreferredOrder.add(FRAME_ID_URL_ARTIST_WEB);
        frameIdsInPreferredOrder.add(FRAME_ID_URL_COMMERCIAL);
        frameIdsInPreferredOrder.add(FRAME_ID_URL_COPYRIGHT);
        frameIdsInPreferredOrder.add(FRAME_ID_URL_FILE_WEB);
        frameIdsInPreferredOrder.add(FRAME_ID_URL_OFFICIAL_RADIO);
        frameIdsInPreferredOrder.add(FRAME_ID_URL_PAYMENT);
        frameIdsInPreferredOrder.add(FRAME_ID_URL_PUBLISHERS);
        frameIdsInPreferredOrder.add(FRAME_ID_URL_COMMERCIAL);
        frameIdsInPreferredOrder.add(FRAME_ID_LYRICIST);
        frameIdsInPreferredOrder.add(FRAME_ID_MEDIA_TYPE);
        frameIdsInPreferredOrder.add(FRAME_ID_INVOLVED_PEOPLE);
        frameIdsInPreferredOrder.add(FRAME_ID_LANGUAGE);
        frameIdsInPreferredOrder.add(FRAME_ID_ARTIST_SORT_ORDER);
        frameIdsInPreferredOrder.add(FRAME_ID_PLAYLIST_DELAY);
        frameIdsInPreferredOrder.add(FRAME_ID_PLAY_COUNTER);
        frameIdsInPreferredOrder.add(FRAME_ID_POPULARIMETER);
        frameIdsInPreferredOrder.add(FRAME_ID_PUBLISHER);
        frameIdsInPreferredOrder.add(FRAME_ID_ALBUM_ARTIST_SORT_ORDER_ITUNES);
        frameIdsInPreferredOrder.add(FRAME_ID_COMPOSER_SORT_ORDER_ITUNES);
        frameIdsInPreferredOrder.add(FRAME_ID_IS_COMPILATION);
        frameIdsInPreferredOrder.add(FRAME_ID_COMMENT);

        //Not so bothered about these
        frameIdsInPreferredOrder.add(FRAME_ID_AUDIO_SEEK_POINT_INDEX);
        frameIdsInPreferredOrder.add(FRAME_ID_COMMERCIAL_FRAME);
        frameIdsInPreferredOrder.add(FRAME_ID_COPYRIGHTINFO);
        frameIdsInPreferredOrder.add(FRAME_ID_ENCODEDBY);
        frameIdsInPreferredOrder.add(FRAME_ID_ENCODING_TIME);
        frameIdsInPreferredOrder.add(FRAME_ID_ENCRYPTION);
        frameIdsInPreferredOrder.add(FRAME_ID_EQUALISATION2);
        frameIdsInPreferredOrder.add(FRAME_ID_EVENT_TIMING_CODES);
        frameIdsInPreferredOrder.add(FRAME_ID_FILE_OWNER);
        frameIdsInPreferredOrder.add(FRAME_ID_FILE_TYPE);
        frameIdsInPreferredOrder.add(FRAME_ID_GROUP_ID_REG);
        frameIdsInPreferredOrder.add(FRAME_ID_HW_SW_SETTINGS);
        frameIdsInPreferredOrder.add(FRAME_ID_INITIAL_KEY);
        frameIdsInPreferredOrder.add(FRAME_ID_LENGTH);
        frameIdsInPreferredOrder.add(FRAME_ID_LINKED_INFO);
        frameIdsInPreferredOrder.add(FRAME_ID_MOOD);
        frameIdsInPreferredOrder.add(FRAME_ID_MPEG_LOCATION_LOOKUP_TABLE);
        frameIdsInPreferredOrder.add(FRAME_ID_MUSICIAN_CREDITS);
        frameIdsInPreferredOrder.add(FRAME_ID_ORIGARTIST);
        frameIdsInPreferredOrder.add(FRAME_ID_ORIGINAL_RELEASE_TIME);
        frameIdsInPreferredOrder.add(FRAME_ID_ORIG_FILENAME);
        frameIdsInPreferredOrder.add(FRAME_ID_ORIG_LYRICIST);
        frameIdsInPreferredOrder.add(FRAME_ID_ORIG_TITLE);
        frameIdsInPreferredOrder.add(FRAME_ID_OWNERSHIP);
        frameIdsInPreferredOrder.add(FRAME_ID_POSITION_SYNC);
        frameIdsInPreferredOrder.add(FRAME_ID_PRODUCED_NOTICE);
        frameIdsInPreferredOrder.add(FRAME_ID_RADIO_NAME);
        frameIdsInPreferredOrder.add(FRAME_ID_RADIO_OWNER);
        frameIdsInPreferredOrder.add(FRAME_ID_RECOMMENDED_BUFFER_SIZE);
        frameIdsInPreferredOrder.add(FRAME_ID_RELATIVE_VOLUME_ADJUSTMENT2);
        frameIdsInPreferredOrder.add(FRAME_ID_RELEASE_TIME);
        frameIdsInPreferredOrder.add(FRAME_ID_REMIXED);
        frameIdsInPreferredOrder.add(FRAME_ID_REVERB);
        frameIdsInPreferredOrder.add(FRAME_ID_SEEK);
        frameIdsInPreferredOrder.add(FRAME_ID_SET);
        frameIdsInPreferredOrder.add(FRAME_ID_SET_SUBTITLE);
        frameIdsInPreferredOrder.add(FRAME_ID_SIGNATURE);
        frameIdsInPreferredOrder.add(FRAME_ID_SYNC_LYRIC);
        frameIdsInPreferredOrder.add(FRAME_ID_SYNC_TEMPO);
        frameIdsInPreferredOrder.add(FRAME_ID_TAGGING_TIME);
        frameIdsInPreferredOrder.add(FRAME_ID_TERMS_OF_USE);

        //Want this near the end because can cause problems with unsyncing
        frameIdsInPreferredOrder.add(FRAME_ID_ATTACHED_PICTURE);

        //Itunes doesnt seem to like these, and of little use so put right at end
        frameIdsInPreferredOrder.add(FRAME_ID_PRIVATE);
        frameIdsInPreferredOrder.add(FRAME_ID_MUSIC_CD_ID);
        frameIdsInPreferredOrder.add(FRAME_ID_AUDIO_ENCRYPTION);
        frameIdsInPreferredOrder.add(FRAME_ID_GENERAL_ENCAPS_OBJECT);

    }

    private ID3v24PreferredFrameOrderComparator() {

    }

    public static ID3v24PreferredFrameOrderComparator getInstanceof() {
        if (comparator == null) {
            comparator = new ID3v24PreferredFrameOrderComparator();
        }
        return comparator;
    }


    /**
     * @param frameId1
     * @param frameId2
     * @return
     */
    public int compare(String frameId1, String frameId2) {
        int frameId1Index = frameIdsInPreferredOrder.indexOf(frameId1);
        if (frameId1Index == -1) {
            frameId1Index = Integer.MAX_VALUE;
        }
        int frameId2Index = frameIdsInPreferredOrder.indexOf(frameId2);

        //Because othwerwise returns -1 whihc would be tags in list went to top of list
        if (frameId2Index == -1) {
            frameId2Index = Integer.MAX_VALUE;
        }

        //To have determinable ordering AND because if returns equal Treese considers as equal
        if (frameId1Index == frameId2Index) {
            return frameId1.compareTo(frameId2);
        }
        return frameId1Index - frameId2Index;
    }

    public boolean equals(Object obj) {
        return obj instanceof ID3v24PreferredFrameOrderComparator;
    }

}
