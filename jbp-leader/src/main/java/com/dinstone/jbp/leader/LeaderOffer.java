
package com.dinstone.jbp.leader;

import java.util.Comparator;

public class LeaderOffer {

    /** 编号 */
    private Integer code;

    /** 提议 */
    private String offer;

    /** 候选者 */
    private String candidate;

    public LeaderOffer(Integer code, String offer, String candidate) {
        super();
        this.code = code;
        this.offer = offer;
        this.candidate = candidate;
    }

    /**
     * the code to get
     * 
     * @return the code
     * @see LeaderOffer#code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * the offer to get
     * 
     * @return the offer
     * @see LeaderOffer#offer
     */
    public String getOffer() {
        return offer;
    }

    /**
     * the candidate to get
     * 
     * @return the candidate
     * @see LeaderOffer#candidate
     */
    public String getCandidate() {
        return candidate;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LeaderOffer [code=" + code + ", offer=" + offer + ", candidate=" + candidate + "]";
    }

    public static class LeaderOfferComparator implements Comparator<LeaderOffer> {

        public int compare(LeaderOffer o1, LeaderOffer o2) {
            return o1.getCode().compareTo(o2.getCode());
        }

    }

}
