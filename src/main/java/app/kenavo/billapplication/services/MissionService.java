package app.kenavo.billapplication.services;

import app.kenavo.billapplication.model.Mission;

import java.util.List;

public interface MissionService {

    public List<Mission> getAllMissions();

    public List<Mission> getAllMissionsByAccount(List<Mission> missions, String accountId);

    public List<Mission> getAllMissionsByBill(List<Mission> missions, String billId);

    public Mission getMissionById(List<Mission> missions, String id);

    public Mission create(Mission mission);

    public void delete(List<Mission> missions, Mission mission);

    public void update(List<Mission> missions, List<Mission> missionsToUpdate);
}
