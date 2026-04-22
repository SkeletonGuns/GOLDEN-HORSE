package com.example.backendapp.services;

import com.example.backendapp.models.*;
import com.example.backendapp.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

@Service

public class gameService {
    private final userRepository userRepository;
    private final roomRepository roomRepository;
    private final participationRepository participationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public gameService(userRepository userRepository,
                       roomRepository roomRepository,
                       participationRepository participationRepository,
                       @org.springframework.context.annotation.Lazy SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.participationRepository = participationRepository;
        this.messagingTemplate = messagingTemplate;
    }
    @Transactional
    public void joinRoom(Long userId, Long roomId, Integer prediction) {
        user user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь с ID " + userId + " не найден!"));

        room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Комната с ID " + roomId + " не найдена!"));

        if (user.getBonusBalance() < room.getEntryPrice()) {
            throw new RuntimeException("Недостаточно баллов");
        }

        user.setBonusBalance(user.getBonusBalance() - room.getEntryPrice());
        user.setReservedBalance(user.getReservedBalance() + room.getEntryPrice());
        userRepository.save(user);

        Participation participation = new Participation();
        participation.setUser(user);
        participation.setRoom(room);
        participation.setPredictedNumber(prediction);
        participationRepository.save(participation);
    }

    @Transactional
    public void finishRound(Long roomId) {
        room room = roomRepository.findById(roomId).orElseThrow();
        if (!"WAITING".equals(room.getStatus())) return;

        List<Participation> participants = participationRepository.findAllByRoomId(roomId);

        if (participants.isEmpty()) {
            room.setStatus("FINISHED");
            roomRepository.save(room);
            return;
        }

        double[] weights = new double[6];
        for (int i = 1; i <= 5; i++) weights[i] = 1.0;

        for (Participation p : participants) {
            if (p.getPredictedNumber() != null && p.getPredictedNumber() >= 1 && p.getPredictedNumber() <= 5) {
                double weightContribution = p.isHasBoost() ? 2.5 : 1.0;
                weights[p.getPredictedNumber()] += weightContribution;
            }
        }

        int winningNumber = calculateWeightedWinner(weights);
        System.out.println("Веса номеров: " + java.util.Arrays.toString(weights));
        System.out.println("Выигрышный номер (с учетом веса): " + winningNumber);

        double prizePool = participants.size() * room.getEntryPrice() * (room.getPrizePoolPercentage() / 100.0);

        List<Participation> winners = participants.stream()
                .filter(p -> p.getPredictedNumber() == winningNumber)
                .toList();

        if (!winners.isEmpty()) {
            double prizePerPerson = prizePool / winners.size();
            for (Participation p : winners) {
                user u = p.getUser();
                u.setBonusBalance(u.getBonusBalance() + (long)prizePerPerson);
                userRepository.save(u);
            }
            messagingTemplate.convertAndSend("/topic/game-events",
                    "Финиш! Победила лошадь №" + winningNumber + ". Выигравших: " + winners.size());
        } else {
            messagingTemplate.convertAndSend("/topic/game-events", "Выпал номер " + winningNumber + ". Победил бот.");
        }

        for (Participation p : participants) {
            user u = p.getUser();
            u.setReservedBalance(Math.max(0, u.getReservedBalance() - room.getEntryPrice()));
            userRepository.save(u);
        }

        double prizePerPerson = winners.isEmpty() ? 0 : prizePool / winners.size();

        for (Participation p : participants) {
            p.setFinished(true);

            if (p.getPredictedNumber() == winningNumber) {
                p.setWinAmount((int) prizePerPerson);

                user u = p.getUser();
                u.setBonusBalance(u.getBonusBalance() + (long) prizePerPerson);
                userRepository.save(u);
            } else {
                p.setWinAmount(0);
            }
            participationRepository.save(p);
        }

        room.setStatus("FINISHED");
        roomRepository.save(room);

    }

    private int calculateWeightedWinner(double[] weights) {
        double totalWeight = 0;
        for (int i = 1; i <= 5; i++) totalWeight += weights[i];

        double randomValue = Math.random() * totalWeight;
        double currentSum = 0;

        for (int i = 1; i <= 5; i++) {
            currentSum += weights[i];
            if (randomValue <= currentSum) {
                return i;
            }
        }
        return 1;
    }

    @Transactional
    public void activateBoost(Long userId, Long roomId) {
        Participation participation = participationRepository.findByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new RuntimeException("Вы еще не участвуете в этом забеге!"));

        if (participation.isHasBoost()) {
            throw new RuntimeException("Буст уже куплен для этой ставки");
        }

        user user = userRepository.findById(userId).orElseThrow();
        room room = roomRepository.findById(roomId).orElseThrow();

        if (user.getBonusBalance() < room.getBoostCost()) {
            throw new RuntimeException("Недостаточно бонусов для буста");
        }

        user.setBonusBalance(user.getBonusBalance() - room.getBoostCost());
        participation.setHasBoost(true);

        userRepository.save(user);
        participationRepository.save(participation);
    }
}
