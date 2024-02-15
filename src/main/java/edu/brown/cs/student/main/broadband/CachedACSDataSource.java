package edu.brown.cs.student.main.broadband;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.broadband.exceptions.InputNotFoundException;
import java.util.concurrent.TimeUnit;

public class CachedACSDataSource implements CensusDataSource {

  private final ACSCensusDataSource wrappedACSCensusDataSource;
  private final LoadingCache<String, String> cache;

  public CachedACSDataSource(ACSCensusDataSource toWrap) {
    this.wrappedACSCensusDataSource = toWrap;

    this.cache =
        CacheBuilder.newBuilder()
            // Using the expireAfterWrite and maximumSize methods to configure the cache,
            // and documenting how the developer is able to change these parameters is sufficient
            // to fulfill this spec: https://edstem.org/us/courses/54377/discussion/4348144

            // How many entries maximum in the cache?
            .maximumSize(10)
            // How long should entries remain in the cache?
            .expireAfterWrite(1, TimeUnit.MINUTES)
            // Keep statistical info around for profiling purposes

            .recordStats()
            .build(
                // Strategy pattern: how should the cache behave when
                // it's asked for something it doesn't have?
                new CacheLoader<>() {
                  @Override
                  public String load(String key) {
                    System.out.println("called load for: " + key);
                    // If this isn't yet present in the cache, load it:
                    String[] split_params = key.split(",");
                    if (split_params.length == 2) {
                      String countyname = split_params[0];
                      String statename = split_params[1];
                      //                  Map<String,String> =Collections.unmodifiableMap()
                      try {
                        // return wrappedACSCensusDataSource.getBroadbandPercentage(countyname,
                        // statename);
                        String out =
                            wrappedACSCensusDataSource.getBroadbandPercentage(
                                countyname, statename);
                        return out;
                      } catch (Exception e) {
                        throw new InputNotFoundException(
                            "The input you entered (" + countyname + ", " + statename);
                        // return "";//TODO: may need error handling?
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
                        // return "";//TODO: may need error handling?
                      }
                    }
                  }
                });
  }

  @Override
  public String getBroadbandPercentage(String countyname, String statename) {
    // String public_broadband = Collections.unmodifiable
    String target = countyname + "," + statename;
    String result = cache.getUnchecked(target);
    // For debugging and demo (would remove in a "real" version):
    System.out.println(cache.stats());
    return result;
  }

  @Override
  public String getBroadbandPercentage(String countyname, String statename, String acsvariable) {
    // String public_broadband = Collections.unmodifiable
    String target = countyname + "," + statename + "," + acsvariable;
    String result = cache.getUnchecked(target);
    // For debugging and demo (would remove in a "real" version):
    System.out.println(cache.stats());
    return result;
  }
}
