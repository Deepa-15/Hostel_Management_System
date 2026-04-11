package com.hostel.service;

import com.hostel.model.Hostel;

import java.util.List;
import java.util.Optional;

public interface HostelService {
    Hostel createHostel(Hostel hostel);
    Optional<Hostel> findById(Long id);
    List<Hostel> getAllHostels();
    Hostel updateHostel(Hostel hostel);
    void deleteHostel(Long id);
}
