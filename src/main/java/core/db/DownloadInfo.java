package core.db;

import core.util.HODateTime;

/**
 * Information of the hattrick downloads
 *
 * @param hrfId  Identifier of the download
 * @param date   Date of the download
 * @param nextDailyUpdate Date of the next hattrick's daily update
 */
public record DownloadInfo(int hrfId, HODateTime date, HODateTime nextDailyUpdate) { }
