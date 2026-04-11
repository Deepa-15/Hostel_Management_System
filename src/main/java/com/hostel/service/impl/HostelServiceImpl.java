package com.hostel.service.impl;

import com.hostel.exception.ResourceNotFoundException;
import com.hostel.model.Hostel;
import com.hostel.repository.HostelRepository;
import com.hostel.service.HostelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HostelServiceImpl implements HostelService {

    private final HostelRepository hostelRepository;

    @Autowired
    public HostelServiceImpl(HostelRepository hostelRepository) {
        this.hostelRepository = hostelRepository;
    }

    @Override
    public Hostel createHostel(Hostel hostel) {
        return hostelRepository.save(hostel);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Hostel> findById(Long id) {
        return hostelRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hostel> getAllHostels() {
        return hostelRepository.findAll();
    }

    @Override
    public Hostel updateHostel(Hostel hostel) {
        if (!hostelRepository.existsById(hostel.getId())) {
            throw new ResourceNotFoundException("Hostel", hostel.getId());
        }
        return hostelRepository.save(hostel);
    }

    @Override
    public void deleteHostel(Long id) {
        if (!hostelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hostel", id);
        }
        hostelRepository.deleteById(id);
    }
}
