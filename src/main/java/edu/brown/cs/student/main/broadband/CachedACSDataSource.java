package edu.brown.cs.student.main.broadband;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.broadband.exceptions.InputNotFoundException;
import java.util.concurrent.TimeUnit;

/** The CachedACSDataSource class provides caching functionality for an ACSCensusDataSource. */
public class CachedACSDataSource implements CensusDataSource {

  private final ACSCensusDataSource wrappedACSCensusDataSource;
  private final LoadingCache<String, String> cache;

  /**
   * Construct a new CachedACSDataSource with the specified ACSCensusDataSource to wrap.
   *
   * @param toWrap The ACSCensusDataSource to wrap.
   */
  public CachedACSDataSource(ACSCensusDataSource toWrap) {
    this.wrappedACSCensusDataSource = toWrap;

    this.cache =
        CacheBuilder.newBuilder()
            /* Developer can decide/change how many entries are stored in the cache */
            .maximumSize(3)
            /* Developer can decide/change how long information is stored in the cache */
            .expireAfterWrite(1, TimeUnit.MINUTES)
            /* Keep statistical info around for profiling purposes */
            .recordStats()
            .build(
                new CacheLoader<>() {
                  @Override
                  public String load(String key) {
                    /* If this data isn't yet present in the cache, load it: */
                    String[] split_params = key.split(",");
                    /* check number of parameters (2 or 3) to call correct getBroadbandPercentage method */
                    if (split_params.length == 2) {
                      String countyname = split_params[0];
                      String statename = split_params[1];
                      try {
                        String out =
                            wrappedACSCensusDataSource.getBroadbandPercentage(
                                countyname, statename);
                        return out;
                      } catch (Exception e) {
                        throw new InputNotFoundException(
                            "The input you entered (" + countyname + ", " + statename);
                      }
                    } else {
                      String countyname = split_params[0];
                      String statename = split_params[1];
                      String acsvariable = split_params[2];
                      try {
                        String out =
                            wrappedACSCensusDataSource.getBroadbandPercentage(
                                countyname, statename, acsvariable);
                        return out;
                      } catch (Exception e) {
                        throw new InputNotFoundException(
                            "The input you entered ("
                                + countyname
                                + ", "
                                + statename
                                + ", "
                                + acsvariable);
                      }
                    }
                  }
                });
  }

  /**
   * Retrieves the broadband percentage for a specific county and state by caching.
   *
   * @param countyname The name of the county.
   * @param statename The name of the state.
   * @return The broadband percentage as a String.
   */
  @Override
  public String getBroadbandPercentage(String countyname, String statename) {
    String target = countyname + "," + statename;
    String result = cache.getUnchecked(target);
    return result;
  }

  /**
   * Retrieves the broadband percentage for a specific county, state, and ACS variable, using
   * caching.
   *
   * @param countyname The name of the county.
   * @param statename The name of the state.
   * @param acsvariable The ACS variable to consider.
   * @return The broadband percentage as a String.
   */
  @Override
  public String getBroadbandPercentage(String countyname, String statename, String acsvariable) {
    String target = countyname + "," + statename + "," + acsvariable;
    String result = cache.getUnchecked(target);
    return result;
  }

  /**
   * Retrieves the cache hit count.
   *
   * @return The cache hit count as an int.
   */
  public int getCacheHitCount() {
    int hitcount = (int) cache.stats().hitCount();
    return hitcount;
  }

  /**
   * Retrieves the cache miss count.
   *
   * @return The cache miss count as an int.
   */
  public int getCacheMissCount() {
    int misscount = (int) cache.stats().missCount();
    return misscount;
  }

  /**
   * Retrieves the cache load count.
   *
   * @return The cache load count as an int.
   */
  public int getCacheLoadCount() {
    int loadcount = (int) cache.stats().loadCount();
    return loadcount;
  }

  /**
   * Retrieves the cache eviction count.
   *
   * @return The cache eviction count.
   */
  public int getCacheEvictionCount() {
    int evictioncount = (int) cache.stats().evictionCount();
    return evictioncount;
  }
}
