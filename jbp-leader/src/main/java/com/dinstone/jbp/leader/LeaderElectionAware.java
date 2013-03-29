
package com.dinstone.jbp.leader;

public interface LeaderElectionAware {

    public void produceLeader(LeaderOffer leaderOffer);

    public void produceFollower(LeaderOffer leaderOffer);

    public void electionClosed(LeaderOffer leaderOffer);

    public void exceptionCaught(Throwable cause);
}
