-- 9999999
select MATCHDETAILS.heimid, MATCHDETAILS.gastid, matchhighlights.*
from matchhighlights
         join MATCHDETAILS on MATCHDETAILS.MATCHID = matchhighlights.MATCHID
where match_event_id in (125)
  and (heimid = 9999999 or gastid = 9999999)
order by match_event_id, matchdate;
