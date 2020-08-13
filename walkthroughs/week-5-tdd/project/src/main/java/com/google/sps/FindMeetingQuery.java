// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeSet;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    HashSet<String> mandatoryParticipants = new HashSet<>(request.getAttendees());
    HashSet<String> optionalParticipants = new HashSet<>(request.getOptionalAttendees());
    TreeSet<TimeRange> busyTimesMandatory = new TreeSet<>(TimeRange.ORDER_BY_START);
    TreeSet<TimeRange> busyTimesBoth = new TreeSet<>(TimeRange.ORDER_BY_START);

    for (Event it : events){
      if (eventHasAnyOfGivenParticipants(it.getAttendees(), optionalParticipants) || eventHasAnyOfGivenParticipants(it.getAttendees(), mandatoryParticipants))
        busyTimesBoth.add(it.getWhen());
      if (!eventHasAnyOfGivenParticipants(it.getAttendees(), mandatoryParticipants)) // Do not include event if no mandatory participants attend
        continue;
      busyTimesMandatory.add(it.getWhen());
    }

    Collection<TimeRange> availableTimesForBoth = getPossibleTimesForMeeting(busyTimesBoth, request.getDuration());
    if (availableTimesForBoth.isEmpty())
      return getPossibleTimesForMeeting(busyTimesMandatory, request.getDuration());
    return availableTimesForBoth;
  }

  private Collection<TimeRange> getPossibleTimesForMeeting(Collection<TimeRange> busyTimes, long duration){
    if (busyTimes.isEmpty() && duration <= TimeRange.WHOLE_DAY.duration())
      return Arrays.asList(TimeRange.WHOLE_DAY);
    ArrayList<TimeRange> availableTimes = new ArrayList<>();
    int current = TimeRange.START_OF_DAY;
    
    for (TimeRange busyTime : busyTimes){
      if (busyTime.start() >= current && busyTime.start() - current >= duration)
        availableTimes.add(TimeRange.fromStartEnd(current, busyTime.start(), false));
      if (current< busyTime.end())
      current = busyTime.end();
    }
    if (TimeRange.END_OF_DAY - current >= duration)
      availableTimes.add(TimeRange.fromStartEnd(current, TimeRange.END_OF_DAY, true));

    return availableTimes;
  }

  private boolean eventHasAnyOfGivenParticipants(Collection<String> eventParticipants, Collection<String> givenParticipants){
    for (String participant : eventParticipants)
      if (givenParticipants.contains(participant))
        return true;
    return false;
  }
}
