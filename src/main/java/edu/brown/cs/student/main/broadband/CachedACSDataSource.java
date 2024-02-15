package edu.brown.cs.student.main.broadband;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class CachedACSDataSource implements CensusDataSource {

  private final ACSCensusDataSource wrappedACSCensusDataSource;
  private final LoadingCache<String, String> cache;

  public CachedACSDataSource(ACSCensusDataSource toWrap) {
    this.wrappedACSCensusDataSource = toWrap;

    this.cache = CacheBuilder.newBuilder()
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
                  String countyname = key.split(",")[0];
                  String statename = key.split(",")[1];

                  try {
                      return wrappedACSCensusDataSource.getBroadbandPercentage(countyname, statename);
                  } catch (Exception e) {
                      return "";//TODO: may need error handling?
                  }

              }
            });


  }

  @Override
  public String getBroadbandPercentage(String countyname, String statename){
    //String public_broadband = Collections.unmodifiable
      String target = countyname+","+statename;
      String result = cache.getUnchecked(target);
      // For debugging and demo (would remove in a "real" version):
      System.out.println(cache.stats());
    return result;
  }


}

