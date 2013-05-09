
package com.dinstone.leader;

public interface LeaderElectionAware {

    public void onLeader(LeaderOffer leaderOffer);

    public void onFollower(LeaderOffer leaderOffer);

    public void onClosed(LeaderOffer leaderOffer);

    public void exceptionCaught(Throwable cause);
}
