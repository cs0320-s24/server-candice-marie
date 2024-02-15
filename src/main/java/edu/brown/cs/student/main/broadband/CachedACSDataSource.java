package edu.brown.cs.student.main.broadband;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

//public class CachedACSDataSource implements CensusDataSource {

//  private final ACSCensusDataSource wrappedACSCensusDataSource;
//  private final LoadingCache<String, Collection<String>> cache;
//
//  public CachedACSDataSource(ACSCensusDataSource toWrap) {
//    this.wrappedACSCensusDataSource = toWrap;
//
//    this.cache = CacheBuilder.newBuilder()
//        // How many entries maximum in the cache?
//        .maximumSize(10)
//        // How long should entries remain in the cache?
//        .expireAfterWrite(1, TimeUnit.MINUTES)
//        // Keep statistical info around for profiling purposes
//        .recordStats()
//        .build(
//            // Strategy pattern: how should the cache behave when
//            // it's asked for something it doesn't have?
//            new CacheLoader<>() {
//              @Override
//
//              public Collection<String> load(String countyname) {
//                System.out.println("called load for: " + countyname);
//                // If this isn't yet present in the cache, load it:
//                return wrappedACSCensusDataSource.getBroadbandPercentage(countyname, String statename);
//              }
//            });
//
//
//  }
//
//  @Override
//  public String getBroadbandPercentage(String countyname, String statename) throws Exception {
//    //String public_broadband = Collections.unmodifiable
//    return "";
//  }


//}

