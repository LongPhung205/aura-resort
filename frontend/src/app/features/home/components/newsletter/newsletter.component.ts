import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-newsletter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './newsletter.component.html',
  styleUrls: ['./newsletter.component.scss'],
})
export class NewsletterComponent {
  email = '';
  isSubscribed = false;

  onSubmit(): void {
    if (this.email && this.email.includes('@')) {
      this.isSubscribed = true;
      setTimeout(() => {
        this.isSubscribed = false;
        this.email = '';
      }, 4000);
    }
  }
}
