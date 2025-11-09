 /**
 * Movies Ticket Browser - Main JavaScript File
  * Version: 1.0
 */

// Global application state
const MovieTicketApp = {
    // Initialize the application
    init: function() {
        console.log('Movie Ticket Browser initialized');
        this.setupEventListeners();
        this.initializeComponents();
    },

    // Setup global event listeners
    setupEventListeners: function() {
        // Auto-hide alerts after 5 seconds
        this.autoHideAlerts();

        // Add smooth scrolling
        this.enableSmoothScrolling();
    },

    // Initialize components
    initializeComponents: function() {
        // Initialize Bootstrap components
        this.initializeBootstrapComponents();
    },

    initializeBootstrapComponents: function() {
        // Initialize tooltips
        const tooltipTriggerList = [].slice.call(
            document.querySelectorAll('[data-bs-toggle="tooltip"]')
        );
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });

        // Initialize popovers
        const popoverTriggerList = [].slice.call(
            document.querySelectorAll('[data-bs-toggle="popover"]')
        );
        popoverTriggerList.map(function (popoverTriggerEl) {
            return new bootstrap.Popover(popoverTriggerEl);
        });
    },

    autoHideAlerts: function() {
        const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
        alerts.forEach(alert => {
            setTimeout(() => {
                if (alert && alert.parentNode) {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                }
            }, 5000);
        });
    },

    enableSmoothScrolling: function() {
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                e.preventDefault();
                const target = document.querySelector(this.getAttribute('href'));
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            });
        });
    },

    // Utility functions
    formatCurrency: function(amount) {
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR'
        }).format(amount);
    },

    formatDate: function(dateString) {
        if (!dateString) return 'N/A';
        try {
            const date = new Date(dateString);
            const options = {
                year: 'numeric',
                month: 'short',
                day: 'numeric'
            };
            return date.toLocaleDateString('en-US', options);
        } catch (error) {
            console.error('Date formatting error:', error);
            return dateString;
        }
    },

    showLoading: function(element, text = 'Loading...') {
        if (!element) return null;
        element.disabled = true;
        const originalText = element.innerHTML;
        element.innerHTML = `<i class="fas fa-spinner fa-spin me-2"></i>${text}`;
        return originalText;
    },

    hideLoading: function(element, originalText) {
        if (!element || !originalText) return;
        element.disabled = false;
        element.innerHTML = originalText;
    }
};

// Theater Selection Functions (for booking pages)
const TheaterSelection = {
    init: function(movieId) {
        this.movieId = movieId;
        this.selectedTheaterId = null;
        this.setupTheaterSelection();
        console.log('TheaterSelection initialized for movie:', movieId);
    },

    setupTheaterSelection: function() {
        const theaterCards = document.querySelectorAll('.theater-card');

        theaterCards.forEach(card => {
            card.addEventListener('click', (event) => {
                this.selectTheater(event.currentTarget);
            });
        });

        // Continue button event
        const continueBtn = document.getElementById('continueBtn');
        if (continueBtn) {
            continueBtn.addEventListener('click', () => {
                this.continueToShowtimes();
            });
        }
    },

    selectTheater: function(cardElement) {
        this.selectedTheaterId = cardElement.getAttribute('data-theater-id');

        console.log('Selected theater ID:', this.selectedTheaterId);

        // Update UI
        document.querySelectorAll('.theater-card').forEach(card => {
            card.classList.remove('selected');
        });

        cardElement.classList.add('selected');

        // Update selected theater info
        const theaterName = cardElement.querySelector('h5').textContent;
        const theaterAddress = cardElement.querySelector('.text-muted').textContent;

        const selectedTheaterName = document.getElementById('selectedTheaterName');
        const selectedTheaterAddress = document.getElementById('selectedTheaterAddress');
        const selectedTheaterInfo = document.getElementById('selectedTheaterInfo');

        if (selectedTheaterName) selectedTheaterName.textContent = theaterName;
        if (selectedTheaterAddress) selectedTheaterAddress.textContent = theaterAddress;
        if (selectedTheaterInfo) selectedTheaterInfo.style.display = 'block';

        // Enable continue button
        const continueBtn = document.getElementById('continueBtn');
        if (continueBtn) continueBtn.disabled = false;
    },

    continueToShowtimes: function() {
        if (this.selectedTheaterId && this.movieId) {
            console.log('Continuing to showtimes with movieId:', this.movieId, 'theaterId:', this.selectedTheaterId);
            window.location.href = `/booking/showtime?movieId=${this.movieId}&theaterId=${this.selectedTheaterId}`;
        } else {
            alert('Please select a theater to continue.');
        }
    }
};

// Initialize the main application when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    MovieTicketApp.init();

    // Initialize theater selection if on booking page
    const theaterList = document.getElementById('theatersList');
    if (theaterList) {
        const movieId = document.body.getAttribute('data-movie-id');
        if (movieId) {
            TheaterSelection.init(movieId);
        }
    }
});
/**
 * Show Time Selection Functions
 */
const ShowTimeSelection = {
    init: function(movieId, theaterId) {
        this.movieId = movieId;
        this.theaterId = theaterId;
        this.selectedShowId = null;

        console.log('ShowTimeSelection initialized for movie:', movieId, 'theater:', theaterId);
        this.setupShowTimeSelection();
    },

    setupShowTimeSelection: function() {
        // Set today's date as default
        const today = new Date();
        const todayFormatted = today.toISOString().split('T')[0];
        const datePicker = document.getElementById('datePicker');

        if (datePicker) {
            datePicker.value = todayFormatted;

            // Add event listener for date change
            datePicker.addEventListener('change', () => {
                this.filterShowsByDate();
            });
        }

        // Filter shows by today's date initially
        this.filterShowsByDate();

        // Add click handlers to time slots
        this.setupTimeSlotListeners();

        // Continue button event
        const continueBtn = document.getElementById('continueBtn');
        if (continueBtn) {
            continueBtn.addEventListener('click', () => {
                this.continueToSeats();
            });
        }
    },

    setupTimeSlotListeners: function() {
        document.querySelectorAll('.time-slot').forEach(slot => {
            slot.addEventListener('click', (event) => {
                if (event.currentTarget.classList.contains('disabled')) return;
                this.selectShowTime(event.currentTarget);
            });
        });
    },

    filterShowsByDate: function() {
        const selectedDate = document.getElementById('datePicker').value;
        console.log('Filtering shows for date:', selectedDate);

        document.querySelectorAll('.time-slot').forEach(slot => {
            const showDate = slot.getAttribute('data-date');
            if (showDate === selectedDate) {
                slot.style.display = 'block';
                slot.classList.remove('disabled');
            } else {
                slot.style.display = 'none';
                slot.classList.add('disabled');
            }
        });

        // Reset selection when date changes
        this.selectedShowId = null;
        document.getElementById('continueBtn').disabled = true;
        document.getElementById('selectedShowInfo').style.display = 'none';
    },

    selectShowTime: function(element) {
        this.selectedShowId = element.getAttribute('data-show-id');

        console.log('Selected show ID:', this.selectedShowId);

        // Update UI
        document.querySelectorAll('.time-slot').forEach(slot => {
            slot.classList.remove('selected');
        });
        element.classList.add('selected');

        // Update selected show info
        const showTime = element.querySelector('.time').textContent;
        const showDate = element.querySelector('small').textContent;

        document.getElementById('selectedShowTime').textContent = showTime;
        document.getElementById('selectedShowDate').textContent = showDate;
        document.getElementById('selectedShowInfo').style.display = 'block';

        // Enable continue button
        document.getElementById('continueBtn').disabled = false;
    },

    continueToSeats: function() {
        if (this.selectedShowId) {
            console.log('Continuing to seats with showId:', this.selectedShowId);
            window.location.href = '/booking/seats?showId=' + this.selectedShowId;
        } else {
            alert('Please select a show time to continue.');
        }
    }
};

// Update the main initialization in script.js
document.addEventListener('DOMContentLoaded', function() {
    MovieTicketApp.init();

    // Initialize theater selection if on booking page
    const theaterList = document.getElementById('theatersList');
    if (theaterList) {
        const movieId = document.body.getAttribute('data-movie-id');
        if (movieId) {
            TheaterSelection.init(movieId);
        }
    }

    // Initialize show time selection if on showtime page
    const showTimesContainer = document.getElementById('showTimesContainer');
    if (showTimesContainer) {
        const movieId = document.body.getAttribute('data-movie-id');
        const theaterId = document.body.getAttribute('data-theater-id');
        if (movieId && theaterId) {
            ShowTimeSelection.init(movieId, theaterId);
        }
    }
});
// Seat Selection Functions (for booking pages)
const SeatSelection = {
    init: function(showId) {
        this.showId = showId;
        this.selectedSeats = [];
        this.seatPrices = {
            'CLASSIC': 200,
            'PREMIUM': 350
        };
        this.setupSeatSelection();
        console.log('SeatSelection initialized for show:', showId);
    },

    setupSeatSelection: function() {
        const seatsContainer = document.getElementById('seatsContainer');
        if (!seatsContainer) return;

        const seatElements = seatsContainer.querySelectorAll('.seat.available');

        seatElements.forEach(seat => {
            seat.addEventListener('click', (event) => {
                this.selectSeat(event.currentTarget);
            });
        });

        // Form submission handler
        const bookingForm = document.getElementById('bookingForm');
        if (bookingForm) {
            bookingForm.addEventListener('submit', (e) => {
                if (this.selectedSeats.length === 0) {
                    e.preventDefault();
                    alert('Please select at least one seat.');
                    return false;
                }
                return true;
            });
        }

        this.updateBookingSummary();
    },

    selectSeat: function(seatElement) {
        const seatNo = seatElement.getAttribute('data-seat-no');
        const seatType = seatElement.getAttribute('data-seat-type');
        const price = parseInt(seatElement.getAttribute('data-price'));
        const isAvailable = seatElement.getAttribute('data-available') === 'true';

        if (!isAvailable) {
            return; // Don't allow selection of occupied seats
        }

        // Check if seat is already selected
        const isSelected = this.selectedSeats.some(seat => seat.seatNo === seatNo);

        if (isSelected) {
            // Deselect seat
            seatElement.classList.remove('selected');
            this.selectedSeats = this.selectedSeats.filter(seat => seat.seatNo !== seatNo);
        } else {
            // Select seat
            seatElement.classList.add('selected');
            this.selectedSeats.push({ seatNo, seatType, price });
        }

        this.updateBookingSummary();
    },

    updateBookingSummary: function() {
        const selectedSeatsList = document.getElementById('selectedSeatsList');
        const priceBreakdown = document.getElementById('priceBreakdown');
        const totalAmount = document.getElementById('totalAmount');
        const continueBtn = document.getElementById('continueBtn');
        const selectedSeatsInput = document.getElementById('selectedSeatsInput');

        if (!selectedSeatsList || !priceBreakdown || !totalAmount || !continueBtn || !selectedSeatsInput) {
            return;
        }

        if (this.selectedSeats.length === 0) {
            selectedSeatsList.innerHTML = '<p class="text-muted">No seats selected</p>';
            priceBreakdown.innerHTML = '';
            totalAmount.textContent = '₹0';
            continueBtn.disabled = true;
            selectedSeatsInput.value = '';
            return;
        }

        // Update selected seats list
        selectedSeatsList.innerHTML = this.selectedSeats.map(seat =>
            `<span class="badge bg-primary me-1 mb-1">${seat.seatNo}</span>`
        ).join('');

        // Calculate price breakdown
        const classicSeats = this.selectedSeats.filter(seat => seat.seatType === 'CLASSIC');
        const premiumSeats = this.selectedSeats.filter(seat => seat.seatType === 'PREMIUM');

        let breakdownHTML = '';
        if (classicSeats.length > 0) {
            breakdownHTML += `<small>Classic (${classicSeats.length} × ₹${this.seatPrices.CLASSIC}): ₹${classicSeats.length * this.seatPrices.CLASSIC}</small><br>`;
        }
        if (premiumSeats.length > 0) {
            breakdownHTML += `<small>Premium (${premiumSeats.length} × ₹${this.seatPrices.PREMIUM}): ₹${premiumSeats.length * this.seatPrices.PREMIUM}</small><br>`;
        }

        priceBreakdown.innerHTML = breakdownHTML;

        // Calculate total
        const total = this.selectedSeats.reduce((sum, seat) => sum + seat.price, 0);
        totalAmount.textContent = `₹${total}`;

        // Enable continue button
        continueBtn.disabled = false;

        // Set hidden input value
        selectedSeatsInput.value = this.selectedSeats.map(seat => seat.seatNo).join(',');
    }
};

// Update the main initialization in script.js to include seat selection
document.addEventListener('DOMContentLoaded', function() {
    MovieTicketApp.init();

    // Initialize theater selection if on booking page
    const theaterList = document.getElementById('theatersList');
    if (theaterList) {
        const movieId = document.body.getAttribute('data-movie-id');
        if (movieId) {
            TheaterSelection.init(movieId);
        }
    }

    // Initialize seat selection if on seats page
    const seatsContainer = document.getElementById('seatsContainer');
    if (seatsContainer) {
        const showId = document.body.getAttribute('data-show-id');
        if (showId) {
            SeatSelection.init(showId);
        }
    }
});

// Update main initialization
document.addEventListener('DOMContentLoaded', function() {
    MovieTicketApp.init();

    // ... existing initializations ...

    // Initialize payment processing if on payment page
    const paymentForm = document.getElementById('paymentForm');
    if (paymentForm) {
        PaymentProcessing.init();
    }
});

// Export for global access
window.PaymentProcessing = PaymentProcessing;


// Global error handler
window.addEventListener('error', function(e) {
    console.error('Global error caught:', e.error);
});

// Export for global access (if needed)
window.MovieTicketApp = MovieTicketApp;
window.TheaterSelection = TheaterSelection;
window.ShowTimeSelection = ShowTimeSelection;
window.SeatSelection = SeatSelection;
