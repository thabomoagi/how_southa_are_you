package com.thabo.howsouthaareyou.thirtyseconds.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ThirtySecondsPromptProvider {

    private static final List<String> PROMPTS = List.of(
            "Name as many South African slang words as possible",
            "Name as many braai foods as possible",
            "Name as many South African cities as possible",
            "Name as many South African towns as possible",
            "Name as many provinces as possible",
            "Name as many South African languages as possible",
            "Name as many local music artists as possible",
            "Name as many South African TV shows as possible",
            "Name as many South African movies as possible",
            "Name as many local radio stations as possible",
            "Name as many taxi hand signals as possible",
            "Name as many things you can buy at a spaza shop",
            "Name as many street foods you can find in South Africa",
            "Name as many traditional South African dishes",
            "Name as many South African desserts",
            "Name as many types of boerewors",
            "Name as many things needed for a braai",
            "Name as many reasons a taxi might stop",
            "Name as many things people say when greeting in South Africa",
            "Name as many words South Africans use for friend",
            "Name as many South African rugby players",
            "Name as many Springbok players",
            "Name as many Bafana Bafana players",
            "Name as many Proteas cricket players",
            "Name as many South African sports teams",
            "Name as many stadiums in South Africa",
            "Name as many South African beaches",
            "Name as many South African landmarks",
            "Name as many animals you might see in Kruger National Park",
            "Name as many South African birds",
            "Name as many things affected by load shedding",
            "Name as many household items that use electricity",
            "Name as many South African celebrations",
            "Name as many traditional dances",
            "Name as many local games children play",
            "Name as many things you take to a picnic",
            "Name as many popular road trip snacks",
            "Name as many South African destinations",
            "Name as many things you see at a robot",
            "Name as many reasons someone might be late in South Africa",
            "Name as many jobs people do in the city",
            "Name as many things sold by street vendors",
            "Name as many South African fashion brands",
            "Name as many local hairstyles",
            "Name as many South African musical genres",
            "Name as many instruments used in local music",
            "Name as many famous South African comedians",
            "Name as many things that make a party lekker",
            "Name as many items in a school lunchbox",
            "Name as many things you pack for a December holiday");

    public List<String> getRandomPrompts(int count) {
        List<String> shuffled = new ArrayList<>(PROMPTS);
        Collections.shuffle(shuffled);

        List<String> result = new ArrayList<>();

        while (result.size() < count) {
            if (result.size() % shuffled.size() == 0) {
                Collections.shuffle(shuffled);
            }

            result.add(shuffled.get(result.size() % shuffled.size()));
        }

        return result;
    }
}