package axe170009;

/*
    UNKNOWN - Initially all the processes will have status as unknown
    LEADER - After diam rounds one process will status as Leader
    NON_LEADER - After diam rounds rest of the processes will status as Leader
*/
public enum Status {
    UNKNOWN, LEADER, NON_LEADER
}
